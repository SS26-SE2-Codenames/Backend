package com.codenames.codenames_backend.serialization;

import com.codenames.codenames_backend.playingfield.Card;
import com.codenames.codenames_backend.playingfield.GameManager;
import java.util.ArrayList;
import java.util.List;

public class DataTransferObjectService {
  private CardDataTransferObject createCardDataTransferObject(Card card, Role role) {
    String displayColor;

    if (role == Role.SPYMASTER || card.getIsGuessed()) {
      displayColor = card.getColor().toString();
    } else {
      displayColor = "HIDDEN";
    }
    return new CardDataTransferObject(card.getWord(), displayColor, card.getIsGuessed());
  }

  public GameStateDataTransferObject createGameStateDataTransferObject(
      GameManager gameManager, Role role, String currentTurn) {

    List<Card> cardList = gameManager.getCardList();
    List<CardDataTransferObject> cardDataTransferObject = new ArrayList<>();
    for (Card card : cardList) {
      cardDataTransferObject.add(createCardDataTransferObject(card, role));
    }
    String winner;
    if (gameManager.getWinner() == null) {
      winner = null;
    } else {
      winner = gameManager.getWinner().toString();
    }
    return new GameStateDataTransferObject(
        gameManager.getCurrentRedFound(),
        gameManager.getCurrentBlueFound(),
        cardDataTransferObject,
        winner,
        currentTurn);
  }

  public enum Role {
    SPYMASTER,
    OPERATIVE
  }
}
