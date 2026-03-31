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
  // Testing purposes only, waititng for turn system implementation:
  private int currentRedFound = 0;
  private int currentBlueFound = 0;

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

  /** Returns the current list of cards in a board. */
  public List<Card> getCardList() {
    return this.board.getCardList();
  }

  /** Returns the color of a card. */
  public Color checkColor(int position) {
    return this.board.checkColor(position);
  }
}
