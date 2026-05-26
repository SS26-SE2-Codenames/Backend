package com.codenames.codenames.backend.serialization;

import com.codenames.codenames.backend.game.dto.ClueDto;
import com.codenames.codenames.backend.game.domain.Card;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** Service to create the DTO for the game state, which is then serialized into JSON. */
@Service
public class DataTransferObjectService {

  /**
   * Helper method to create a card DTO with the correct visibility based on role and guess state.
   *
   * @param card card object from the board
   * @return the card DTO for the game state DTO
   */
  private CardDataTransferObject createCardDataTransferObject(Card card) {
    Color displayColor = card.getColor();
    return new CardDataTransferObject(card.getWord(), displayColor, card.isGuessed());
  }

  /**
   * Creates the game state DTO that needs to be serialized into JSON.
   *
   * @param gameManager the game manager that holds the state of the game
   * @param currentTurn the current turn
   * @return a DTO of the current game state
   */
  public GameStateDataTransferObject createGameStateDataTransferObject(
      GameManager gameManager, Team currentTurn, Role currentPhase) {

    List<Card> cardList = gameManager.getCardList();
    List<CardDataTransferObject> cardDataTransferObject = new ArrayList<>();
    for (Card card : cardList) {
      cardDataTransferObject.add(createCardDataTransferObject(card));
    }
    if (gameManager.getCurrentClue() == null) {
      return new GameStateDataTransferObject(
          gameManager.getWinner(),
          currentTurn,
          currentPhase,
          null,
              cardDataTransferObject);
    }
    String word = gameManager.getCurrentClue().word();
    int guessAmount = gameManager.getCurrentClue().guessAmount();
    return new GameStateDataTransferObject(
        gameManager.getWinner(),
        currentTurn,
        currentPhase,
        new ClueDto(word, guessAmount),
            cardDataTransferObject);
  }
}
