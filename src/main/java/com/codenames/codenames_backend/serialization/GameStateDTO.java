package com.codenames.codenames_backend.serialization;

import com.codenames.codenames_backend.playingfield.Card;
import java.util.List;

public class GameStateDTO {
  private final int currentRedFound;
  private final int currentBlueFound;
  private final List<Card> cardList;

  public GameStateDTO(int currentRedFound, int currentBlueFound, List<Card> cardList) {
    this.currentRedFound = currentRedFound;
    this.currentBlueFound = currentBlueFound;
    this.cardList = cardList;
  }

  public int getCurrentRedFound() {
    return currentRedFound;
  }

  public int getCurrentBlueFound() {
    return currentBlueFound;
  }

  public List<Card> getCardList() {
    return cardList;
  }
}
