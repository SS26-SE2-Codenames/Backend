package com.codenames.codenames.backend.lobby.api;

import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.lobby.api.dto.JoinMessage;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Player;
import com.codenames.codenames.backend.shared.websocket.SessionRegistry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling real-time game interactions.
 *
 * <p>Processes client messages (e.g. join requests), coordinates with {@link LobbyService}, and
 * broadcasts updates to subscribed clients.
 */
@Slf4j
@Controller
public class LobbySocketController {

  private final LobbyService lobbyService;
  private final GameService gameService;
  private final SimpMessagingTemplate messagingTemplate;
  private final SessionRegistry sessionRegistry;

  /**
   * Creates a new {@code LobbySocketController}.
   *
   * @param lobbyService the service handling lobby operations
   * @param gameService the service handling game state retrieval
   * @param messagingTemplate the messaging template used for broadcasting updates
   * @param sessionRegistry the registry managing WebSocket sessions
   */
  public LobbySocketController(
      LobbyService lobbyService,
      GameService gameService,
      SimpMessagingTemplate messagingTemplate,
      SessionRegistry sessionRegistry) {
    this.lobbyService = lobbyService;
    this.gameService = gameService;
    this.messagingTemplate = messagingTemplate;
    this.sessionRegistry = sessionRegistry;
  }

  /**
   * Processes a WebSocket request to join a lobby.
   *
   * <p>Registers the player, associates the session with the lobby, and broadcasts the updated
   * player list to subscribers.
   *
   * @param message the join request containing username and lobby code
   * @param headerAccessor provides access to the WebSocket session
   */
  @MessageMapping("/join")
  public void join(JoinMessage message, SimpMessageHeaderAccessor headerAccessor) {

    String sessionId = headerAccessor.getSessionId();
    try {
      if (sessionId == null && headerAccessor.getSessionAttributes() != null) {
        sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");
      }

      if (sessionId == null) {
        return;
      }
    } catch (NullPointerException e) {
      log.error(e.getMessage());
    }

    Player joinedPlayer = lobbyService.joinLobby(
            message.getName(),
            message.getCode(),
            message.getUuid()
    );
    boolean joined = (joinedPlayer != null);
    boolean reconnect =
        lobbyService.getPlayers(message.getCode()).stream()
            .anyMatch(player -> player.uuid().equals(message.getUuid()));

    if (!joined && !reconnect) {
      messagingTemplate.convertAndSend("/topic/errors/" + sessionId, "Join failed");
      return;
    }

    sessionRegistry.register(sessionId, message.getName(), message.getCode());


    sendPlayerUpdate(message.getCode());
    sendGameStateUpdate(message.getCode());
  }

  /**
   * Sends the updated list of player usernames to all clients in the lobby.
   *
   * @param code the lobby code identifying the lobby
   */
  private void sendPlayerUpdate(String code) {
    List<String> players = lobbyService.getPlayers(code).stream().map(Player::username).toList();

    messagingTemplate.convertAndSend("/topic/lobby/" + code, players);
  }

  /**
   * Sends the current game state to all clients subscribed to the lobby game topic.
   *
   * @param code the lobby code identifying the game
   */
  private void sendGameStateUpdate(String code) {
    messagingTemplate.convertAndSend("/topic/game/" + code, gameService.getCurrentGameState(code));
  }
}
