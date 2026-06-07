package com.codenames.codenames.backend.game.api;

import com.codenames.codenames.backend.database.persistence.PersistenceService;
import com.codenames.codenames.backend.game.api.dto.ClueMessage;
import com.codenames.codenames.backend.game.api.dto.PassTurnMessage;
import com.codenames.codenames.backend.game.api.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.api.dto.StartGameMessage;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.Clue;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for real-time gameplay interactions.
 *
 * <p>Handles game-related WebSocket requests such as starting games, submitting clues, and
 * revealing cards. Broadcasts updated game state information to subscribed clients.
 */
@Controller
public class GameSocketController {

  private final GameService gameService;

  private final SimpMessagingTemplate messagingTemplate;
  private final PersistenceService persistenceService;

  private static final String GAME_TOPIC_PREFIX = "/topic/game/";

  /**
   * Creates a new {@code GameSocketController}.
   *
   * @param gameService service responsible for gameplay logic
   * @param messagingTemplate template used for broadcasting websocket messages
   * @param persistenceService service used to persist current backend state
   */
  public GameSocketController(
      GameService gameService,
      SimpMessagingTemplate messagingTemplate,
      PersistenceService persistenceService) {

    this.gameService = gameService;
    this.messagingTemplate = messagingTemplate;
    this.persistenceService = persistenceService;
  }

  /**
   * Sends the current game state to subscribed players.
   *
   * @param message contains the lobby code
   */
  @MessageMapping("/start-game")
  public void startGame(StartGameMessage message) {

    messagingTemplate.convertAndSend(
        GAME_TOPIC_PREFIX + message.getLobbyCode(),
        gameService.getCurrentGameState(message.getLobbyCode()));
  }

  /**
   * Reveals a card on the board.
   *
   * <p>Updates the game state and broadcasts the updated board to all subscribed players.
   *
   * @param message the reveal card request
   */
  @MessageMapping("/reveal-card")
  public void revealCard(RevealCardMessage message) {

    gameService.flipCard(message.getLobbyCode(), message.getPosition(), message.getCurrentTurn());

    messagingTemplate.convertAndSend(
        GAME_TOPIC_PREFIX + message.getLobbyCode(),
        gameService.getCurrentGameState(message.getLobbyCode()));
  }

  /**
   * Submits a clue for the current turn.
   *
   * <p>Updates the current clue and broadcasts the updated game state to all subscribed players.
   *
   * @param message the clue submission request
   */
  @MessageMapping("/submit-clue")
  public void submitClue(ClueMessage message) {

    gameService.submitClue(
        message.getLobbyCode(),
        new Clue(message.getWord(), message.getGuessAmount()),
        message.getCurrentTurn());
    persistenceService.saveSnapShot(message.getLobbyCode());

    messagingTemplate.convertAndSend(
        GAME_TOPIC_PREFIX + message.getLobbyCode(),
        gameService.getCurrentGameState(message.getLobbyCode()));
  }

  /**
   * Ends the current turn early and broadcasts the updated game state.
   *
   * @param message contains lobby and team information
   */
  @MessageMapping("/pass-turn")
  public void passTurn(PassTurnMessage message) {

    gameService.passTurn(message.getLobbyCode(), message.getCurrentTurn());
    persistenceService.saveSnapShot(message.getLobbyCode());

    messagingTemplate.convertAndSend(
        GAME_TOPIC_PREFIX + message.getLobbyCode(),
        gameService.getCurrentGameState(message.getLobbyCode()));
  }
}
