package com.codenames.codenames.backend.game.controller;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.game.dto.ClueMessage;
import com.codenames.codenames.backend.game.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.dto.StartGameMessage;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.utility.Team;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * WebSocket controller for real-time gameplay interactions.
 *
 * <p>Handles game-related WebSocket requests such as starting games, submitting clues, and
 * revealing cards. Broadcasts updated game state information to subscribed clients.
 */
public class GameSocketController {

  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;
  private final CardGenerator cardGenerator;
  private final ClueValidationService clueValidationService;

  private final Map<String, GameManager> gameSessions = new ConcurrentHashMap<>();

  /**
   * Creates a new {@code GameSocketController}.
   *
   * @param lobbyService service for lobby management
   * @param messagingTemplate template used for broadcasting messages
   * @param cardGenerator utility for generating game cards
   * @param clueValidationService service for validating clues
   */
  public GameSocketController(
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate,
      CardGenerator cardGenerator,
      ClueValidationService clueValidationService) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
    this.cardGenerator = cardGenerator;
    this.clueValidationService = clueValidationService;
  }

  /**
   * Starts a new game session for a lobby.
   *
   * <p>Creates a new game manager and broadcasts the initial board state to all subscribed players.
   *
   * @param message the start game request
   */
  @MessageMapping("/start-game")
  public void startGame(StartGameMessage message) {

    Team startingTeam = lobbyService.decideStartingTeam(message.getLobbyCode());

    GameManager gameManager = new GameManager(startingTeam, cardGenerator, clueValidationService);

    gameSessions.put(message.getLobbyCode(), gameManager);

    messagingTemplate.convertAndSend(
        "/topic/game/" + message.getLobbyCode(), gameManager.getCardList());
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

    GameManager gameManager = gameSessions.get(message.getLobbyCode());

    if (gameManager == null) {
      return;
    }

    gameManager.flipCard(message.getPosition(), message.getCurrentTurn());

    messagingTemplate.convertAndSend(
        "/topic/game/" + message.getLobbyCode(), gameManager.getCardList());
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

    GameManager gameManager = gameSessions.get(message.getLobbyCode());

    if (gameManager == null) {
      return;
    }

    Clue clue = new Clue(message.getWord(), message.getGuessAmount());

    gameManager.submitClue(clue);

    messagingTemplate.convertAndSend("/topic/game/" + message.getLobbyCode(), gameManager);
  }
}
