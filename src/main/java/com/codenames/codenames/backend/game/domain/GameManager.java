package com.codenames.codenames.backend.game.domain;

import com.codenames.codenames.backend.game.domain.Clue;
import com.codenames.codenames.backend.game.application.ClueValidationService;
import com.codenames.codenames.backend.game.application.CardGenerator;
import com.codenames.codenames.backend.game.api.dto.CardDto;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.domain.Color;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import lombok.Getter;

/**
 * This class handles the setup of the board and interaction, by providing methods to interact with
 * the game's state.
 *
 * <p>Additionally, it keeps track of the points, turn and handles the early determining the winner
 */
public class GameManager {

  private static final int TOTAL_CARDS = 25;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;
  private final int redCards;
  private final int blueCards;

  private final Board board;
  @Getter private int currentRedFound = 0;
  @Getter private int currentBlueFound = 0;
  private Team winner;

  private final ClueValidationService clueValidationService;
  @Getter private Clue currentClue;
  @Getter private int remainingGuesses;

  @Getter private Team currentTurn;
  @Getter private Role currentPhase;

  /**
   * Constructor for a new GameManager and initializes the playing board.
   *
   * @param startingTeam the team that goes first will get an extra card
   * @param cardGenerator the utility used to generate the cards for the game
   * @param clueValidationService the utility used to validate clues
   * @throws IllegalArgumentException if team is null, white or black
   */
  public GameManager(
      Team startingTeam, CardGenerator cardGenerator, ClueValidationService clueValidationService) {
    if (startingTeam == null) {
      throw new IllegalArgumentException("startingTeam cannot be null");
    }
    this.currentTurn = startingTeam;
    this.currentPhase = Role.SPYMASTER; // we hard code spymaster since game has to start with them

    this.clueValidationService = clueValidationService;

    if (startingTeam == Team.RED) {
      this.redCards = 9;
      this.blueCards = 8;
    } else {
      this.redCards = 8;
      this.blueCards = 9;
    }

    this.board =
        new Board(cardGenerator, TOTAL_CARDS, redCards, blueCards, WHITE_CARDS, BLACK_CARDS);
  }

  /**
   * Constructor used by recovery logic to rebuild an already running game state.
   *
   * @param state bundled recovery state
   * @param clueValidationService clue validation service
   */
  public GameManager(
      GameStateDto state, ClueValidationService clueValidationService) {
    if (state.cardList() == null || state.cardList().isEmpty()) {
      throw new IllegalArgumentException("cards cannot be null or empty");
    }
    if (state.currentTurn() == null || state.currentPhase() == null) {
      throw new IllegalArgumentException("current turn and phase cannot be null");
    }

    this.currentTurn = state.currentTurn();
    this.currentPhase = state.currentPhase();
    this.winner = state.winner();
    this.currentClue =
        state.currentClue() == null
            ? null
            : new Clue(state.currentClue().word(), state.currentClue().guessAmount());
    this.clueValidationService = clueValidationService;

    List<Card> cards = state.cardList().stream().map(this::toCard).toList();

    this.currentRedFound = countGuessedByColor(cards, Color.RED);
    this.currentBlueFound = countGuessedByColor(cards, Color.BLUE);

    this.redCards = countCardsByColor(cards, Color.RED);
    this.blueCards = countCardsByColor(cards, Color.BLUE);
    this.board = new Board(cards);
  }

  private Card toCard(CardDto cardDto) {
    Card card = new Card(cardDto.word(), cardDto.color());
    if (cardDto.isGuessed()) {
      card.setIsGuessedTrue();
    }
    return card;
  }

  private int countGuessedByColor(List<Card> cards, Color color) {
    return (int)
        cards.stream().filter(card -> card.isGuessed() && card.getColor() == color).count();
  }

  /**
   * Counts cards of a specific color within the current board snapshot.
   *
   * @param cards recovered cards
   * @param color color to count
   * @return number of cards with the requested color
   */
  private int countCardsByColor(List<Card> cards, Color color) {
    return (int) cards.stream().filter(card -> card.getColor() == color).count();
  }

  /**
   * Returns the current list of cards in a board.
   *
   * @return the list of cards
   */
  public List<Card> getCardList() {
    return this.board.getCardList();
  }

  /**
   * Returns the color of a card.
   *
   * @param position the index of the card on the board
   * @return the color of the requested card
   */
  public Color checkColor(int position) {
    return this.board.checkColor(position);
  }

  /**
   * Updates the score based on the color passed. If black card is found, opposing team wins.
   *
   * @param cardColor the color of the card
   */
  private void updateScore(Color cardColor) {
    switch (cardColor) {
      case RED:
        currentRedFound++;
        break;
      case BLUE:
        currentBlueFound++;
        break;
      case ASSASSIN:
        if (this.currentTurn == Team.RED) {
          this.winner = Team.BLUE;
        } else {
          this.winner = Team.RED;
        }
        break;
      default:
        break;
    }
  }

  /**
   * Checks if win condition has been met or not.
   *
   * @return the winning color is returned or null if no team has won
   */
  public Team getWinner() {
    if (this.winner != null) {
      return this.winner;
    }
    if (currentRedFound >= redCards) {
      return Team.RED;
    }
    if (currentBlueFound >= blueCards) {
      return Team.BLUE;
    }
    return null;
  }

  /**
   * Changes the guessed state of a card and updates the score if necessary.
   *
   * @param position the position of the card that is selected by the player
   * @param callingTeam the team that called this method
   * @throws IllegalStateException if game over, card already flipped, no more guesses
   */
  public void flipCard(int position, Team callingTeam) {
    if (getWinner() != null) {
      throw new IllegalStateException("Winner is already set");
    }
    if (this.board.getIsGuessed(position)) {
      throw new IllegalStateException("Card is already flipped");
    }
    if (this.remainingGuesses <= 0) {
      clearClue();
      throw new IllegalStateException("No more guesses.");
    }

    checkCorrectTurn(callingTeam, Role.OPERATIVE);

    this.remainingGuesses--;
    this.board.setGuessed(position);
    Color currentColor = this.board.checkColor(position);
    updateScore(currentColor);

    boolean opponentOrWhiteCard =
        (currentTurn == Team.RED && currentColor != Color.RED)
            || (currentTurn == Team.BLUE && currentColor != Color.BLUE);

    if (opponentOrWhiteCard || this.remainingGuesses == 0) {
      advanceTurn();
    }
  }

  /**
   * Submits a clue and updates remaining guesses.
   *
   * @param clue the clue object containing word and guess amount
   * @param callingTeam the team that called this method
   * @throws IllegalArgumentException if clue is: null, empty, spaces, or word is on the board
   */
  public void submitClue(Clue clue, Team callingTeam) {
    checkCorrectTurn(callingTeam, Role.SPYMASTER);
    if (clueValidationService.validateWord(this.board, clue.word())) {
      this.currentClue = clue;
      this.remainingGuesses = clue.guessAmount();
      advanceTurn();
    } else {
      throw new IllegalArgumentException("Clue is invalid, cannot be a word that is on the board!");
    }
  }

  /** Clears the current clue and resets guesses to 0. */
  public void clearClue() {
    this.currentClue = null;
    this.remainingGuesses = 0;
  }

  /**
   * Returns the current clue word.
   *
   * @return the current clue word
   */
  public String getCurrentClueWord() {
    if (currentClue == null) {
      return null;
    }
    return currentClue.word();
  }

  /** Changes the color of what team is at turn. */
  private void nextTeamColor() {
    if (currentTurn == Team.RED) {
      currentTurn = Team.BLUE;
    } else {
      currentTurn = Team.RED;
    }
  }

  /**
   * Is called by relevant turn based methods. Class holds the current Team and Phase, when we call
   * this method, based on the current turn, we swap to the opposite turn/ phase. Since after
   * spymaster is done, the same team color is still at turn, and we can simply switch to the
   * operative phase. If we are Operative, we have to additionally switch the next team color and
   * clear the clue from our spymaster.
   */
  public void advanceTurn() {
    if (currentPhase == Role.SPYMASTER) {
      currentPhase = Role.OPERATIVE;
    } else {
      currentPhase = Role.SPYMASTER;
      nextTeamColor();
      clearClue();
    }
  }

  /**
   * Voluntarily pass turn early before all guesses are used up.
   *
   * @param callingTeam the current team calling the method.
   */
  public void passTurn(Team callingTeam) {
    checkCorrectTurn(callingTeam, Role.OPERATIVE);
    advanceTurn();
  }

  /**
   * Helper method to check if the current team calling a method is allowed to do so.
   *
   * @param team the team of who is calling the method
   * @param role the role of who is calling the method
   */
  private void checkCorrectTurn(Team team, Role role) {
    if (team != currentTurn || role != currentPhase) {
      throw new IllegalStateException("Not your turn/ role");
    }
  }
}
