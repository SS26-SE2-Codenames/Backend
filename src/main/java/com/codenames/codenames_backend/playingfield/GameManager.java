package com.codenames.codenames_backend.playingfield;

import java.util.List;

public class GameManager {
  private final int totalCardAmount = 25;
  private Board board;

  public GameManager(Color startingTeam) {
    int redAmount;
    int blueAmount;
    int whiteAmount = 7;
    int blackAmount = 1;
    if (startingTeam == Color.RED) {
      redAmount = 9;
      blueAmount = 8;
    } else {
      redAmount = 8;
      blueAmount = 9;
    }
    this.board = new Board(totalCardAmount, redAmount, blueAmount, whiteAmount, blackAmount);
  }

  public List<Card> getCardList() {
    return this.board.getCardList();
  }

  public String checkColor(int position) {
    return this.board.checkColor(position);
  }
}
