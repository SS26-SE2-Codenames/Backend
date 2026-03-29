package com.codenames.codenames_backend.playingfield;

import java.util.List;

public class GameManager {

  private static final int TOTAL_CARDS = 25;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;

  private final Board board;

  public GameManager(Color startingTeam, CardGenerator cardGenerator) {
    if (startingTeam == null) {
      throw new IllegalArgumentException("startingTeam cannot be null");
    }
    if (startingTeam != Color.RED && startingTeam != Color.BLUE) {
      throw new IllegalArgumentException("startingTeam MUST be Color.RED or Color.BLUE");
    }
    int redAmount;
    int blueAmount;
    if (startingTeam == Color.RED) {
      redAmount = 9;
      blueAmount = 8;
    } else {
      redAmount = 8;
      blueAmount = 9;
    }
    this.board =
        new Board(cardGenerator, TOTAL_CARDS, redAmount, blueAmount, WHITE_CARDS, BLACK_CARDS);
  }

  public List<Card> getCardList() {
    return this.board.getCardList();
  }

  public String checkColor(int position) {
    return this.board.checkColor(position);
  }
}
