package com.codenames.codenames.backend.serialization;

import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.utility.Role;
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

    if (role == Role.SPYMASTER || card.isGuessed()) {
      displayColor = card.getColor().toString();
    } else {
      displayColor = "HIDDEN";
    }
    return new CardDataTransferObject(card.getWord(), displayColor, card.isGuessed());
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
        winner,
        currentTurn,
        gameManager.getCurrentRedFound(),
        gameManager.getCurrentBlueFound(),
        gameManager.getCurrentClueWord(),
        gameManager.getRemainingGuesses(),
        cardDataTransferObject);
  }
}
