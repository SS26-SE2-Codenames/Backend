package com.codenames.codenames_backend.websocket;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listener for WebSocket lifecycle events.
 *
 * <p>Handles events such as client disconnections to ensure proper cleanup of session data and to
 * notify remaining clients in a lobby.
 */
@Component
public class WebSocketEventListener {
  private final SessionRegistry sessionRegistry;
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Creates a new {@code GameController}.
   *
   * @param lobbyService the lobby service handling lobby logic
   * @param messagingTemplate the messaging template used for broadcasting updates
   * @param sessionRegistry the registry tracking WebSocket sessions
   */
  public WebSocketEventListener(
      SessionRegistry sessionRegistry,
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate) {
    this.sessionRegistry = sessionRegistry;
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Handles WebSocket disconnect events.
   *
   * <p>Removes the disconnected player from the lobby, cleans up session mappings, and broadcasts
   * the updated player list to the remaining clients in the lobby.
   *
   * @param event the disconnect event containing the session information
   */
  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {

    String sessionId = event.getSessionId();

    String username = sessionRegistry.getUser(sessionId);
    String lobbyCode = sessionRegistry.getLobby(sessionId);

    if (username == null || lobbyCode == null) {
      return;
    }

    lobbyService.leaveLobby(username, lobbyCode);
    sessionRegistry.remove(sessionId);

    List<String> players =
        lobbyService.getPlayers(lobbyCode).stream().map(Player::getUsername).toList();

    messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode, players);
  }
}
