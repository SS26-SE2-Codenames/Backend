package com.codenames.codenames_backend.websocket;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller responsible for handling game-related messaging.
 *
 * <p>Processes incoming client messages (e.g. join requests) and coordinates lobby interactions via
 * {@link LobbyService}. Also broadcasts updates to subscribed clients using {@link
 * SimpMessagingTemplate}.
 */
@Controller
public class GameController {

  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;
  private final SessionRegistry sessionRegistry;

  /**
   * Creates a new {@code GameController}.
   *
   * @param lobbyService the lobby service handling lobby logic
   * @param messagingTemplate the messaging template used for broadcasting updates
   * @param sessionRegistry the registry tracking WebSocket sessions
   */
  public GameController(
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate,
      SessionRegistry sessionRegistry) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
    this.sessionRegistry = sessionRegistry;
  }

  /**
   * Handles a join request from a client.
   *
   * <p>Registers the player in the lobby, associates the WebSocket session with the player and
   * lobby, and broadcasts the updated player list to all subscribers of the lobby topic.
   *
   * @param message the join message containing the player's name and lobby code
   * @param headerAccessor the accessor used to retrieve WebSocket session attributes
   */
  @MessageMapping("/join")
  public void join(JoinMessage message, SimpMessageHeaderAccessor headerAccessor) {

    String sessionId = headerAccessor.getSessionId();

    lobbyService.joinLobby(message.getName(), message.getCode());

    sessionRegistry.register(sessionId, message.getName(), message.getCode());

    sendPlayerUpdate(message.getCode());
  }

  /**
   * Sends an updated list of player usernames to all clients subscribed to the lobby topic.
   *
   * @param code the lobby code identifying the target lobby
   */
  private void sendPlayerUpdate(String code) {
    List<String> players = lobbyService.getPlayers(code).stream().map(Player::getUsername).toList();

    messagingTemplate.convertAndSend("/topic/lobby/" + code, players);
  }
}
