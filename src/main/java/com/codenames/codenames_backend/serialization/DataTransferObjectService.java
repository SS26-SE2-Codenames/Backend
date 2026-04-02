package com.codenames.codenames_backend.serialization;

import com.codenames.codenames_backend.playingfield.Card;
import com.codenames.codenames_backend.playingfield.GameManager;
import java.util.ArrayList;
import java.util.List;

/** Service to create the DTO for the game state, which is then serialized into JSON. */
public class DataTransferObjectService {

  /**
   * Helper method to create a card DTO with the correct visibility based on role and guess state.
   *
   * @param card card object from the board
   * @param role role of the player, which determines the visibility of the card's color
   * @return the card DTO for the game state DTO
   */
  private CardDataTransferObject createCardDataTransferObject(Card card, Role role) {
    String displayColor;

    if (role == Role.SPYMASTER || card.getIsGuessed()) {
      displayColor = card.getColor().toString();
    } else {
      displayColor = "HIDDEN";
    }
    return new CardDataTransferObject(card.getWord(), displayColor, card.getIsGuessed());
  }

  /**
   * Creates the game state DTO that needs to be serialized into JSON.
   *
   * @param gameManager the game manager that holds the state of the game
   * @param role the role of the player who requires the DTO
   * @param currentTurn the current turn
   * @return a DTO of the current game state
   */
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

  /** Roles of the game, needed for the DTO. */
  public enum Role {
    SPYMASTER,
    OPERATIVE
  }
}
