package com.codenames.codenames_backend.playingfield;

import java.util.List;

/**
 * Manages state and initialization of a game.
 *
 * <p>This class handles the setup of the board and interaction, by providing methods to interact
 * with the game's state.
 */
public class GameManager {

  private static final int TOTAL_CARDS = 25;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;
  private final int redCards;
  private final int blueCards;

  private final Board board;
  private final Color startingTeam;
  private int currentRedFound = 0;
  private int currentBlueFound = 0;
  private Color winner;

  /**
   * Constructor for a new GameManager and initializes the playing board.
   *
   * @param startingTeam the team that goes first will get an extra card
   * @param cardGenerator the utility used to generate the cards for the game
   * @throws IllegalArgumentException if team is null, white or black
   */
  public GameManager(Color startingTeam, CardGenerator cardGenerator) {
    if (startingTeam == null) {
      throw new IllegalArgumentException("startingTeam cannot be null");
    }
    if (startingTeam != Color.RED && startingTeam != Color.BLUE) {
      throw new IllegalArgumentException("startingTeam MUST be Color.RED or Color.BLUE");
    }

    if (startingTeam == Color.RED) {
      this.redCards = 9;
      this.blueCards = 8;
    } else {
      this.redCards = 8;
      this.blueCards = 9;
    }
    this.startingTeam = startingTeam;
    this.board =
        new Board(cardGenerator, TOTAL_CARDS, redCards, blueCards, WHITE_CARDS, BLACK_CARDS);
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
   * @param currentTurn the current team's turn
   */
  private void updateScore(Color cardColor, Color currentTurn) {
    switch (cardColor) {
      case RED:
        currentRedFound++;
        break;
      case BLUE:
        currentBlueFound++;
        break;
      case BLACK:
        if (currentTurn == Color.RED) {
          this.winner = Color.BLUE;
        } else {
          this.winner = Color.RED;
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
  public Color getWinner() {
    if (this.winner != null) {
      return this.winner;
    }
    if (currentRedFound >= redCards) {
      return Color.RED;
    }
    if (currentBlueFound >= blueCards) {
      return Color.BLUE;
    }
    return null;
  }

  /**
   * Changes the guessed state of a card and updates the score if necessary.
   *
   * @param position the position of the card that is selected by the player
   * @param currentTurn the team whose turn it currently is
   * @throws IllegalStateException if game is over or if card is already flipped
   */
  public void flipCard(int position, Color currentTurn) {
    if (getWinner() != null) {
      throw new IllegalStateException("Winner is already set");
    }
    if (this.board.getIsGuessed(position)) {
      throw new IllegalStateException("Card is already flipped");
    }

    this.board.setGuessed(position);
    Color currentColor = this.board.checkColor(position);
    updateScore(currentColor, currentTurn);
  }
}
