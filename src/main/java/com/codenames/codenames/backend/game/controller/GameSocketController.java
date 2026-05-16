package com.codenames.codenames.backend.game.controller;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.game.dto.ClueMessage;
import com.codenames.codenames.backend.game.dto.GameStateDto;
import com.codenames.codenames.backend.game.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.dto.StartGameMessage;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.utility.Team;
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

  private static final String GAME_TOPIC_PREFIX = "/topic/game/";

  /**
   * Creates a new {@code GameSocketController}.
   *
   * @param lobbyService service for lobby management
   * @param messagingTemplate template used for broadcasting messages
   * @param cardGenerator utility for generating game cards
   * @param clueValidationService service for validating clues
   */
  public GameSocketController(GameService gameService, SimpMessagingTemplate messagingTemplate) {

    this.gameService = gameService;
    this.messagingTemplate = messagingTemplate;
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

    messagingTemplate.convertAndSend(
        GAME_TOPIC_PREFIX + message.getLobbyCode(),
        gameService.createGameStateDto(message.getLobbyCode()));
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
        gameService.createGameStateDto(message.getLobbyCode()));
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

    messagingTemplate.convertAndSend(
        GAME_TOPIC_PREFIX + message.getLobbyCode(),
        gameService.createGameStateDto(message.getLobbyCode()));
  }
}
