package com.codenames.codenames_backend.playingfield;

import java.util.List;

public class Board {
  private final List<Card> cardList;
  private CardGenerator cardGenerator;

  public Board(
      CardGenerator cardGenerator, int totalWords, int red, int blue, int white, int black) {
    this.cardList = cardGenerator.generateCards(totalWords, red, blue, white, black);
  }

  public List<Card> getCardList() {
    return cardList;
  }

  public String checkColor(int position) {
    if (position < 0 || position > cardList.size() - 1) {
      throw new IllegalArgumentException("Invalid position");
    }
    return cardList.get(position).getColor().toString();
  }
}
