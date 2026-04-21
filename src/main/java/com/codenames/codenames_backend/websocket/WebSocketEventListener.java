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
 * <p>Handles client disconnections by removing players from lobbies, cleaning up session mappings,
 * and notifying remaining clients.
 */
@Component
public class WebSocketEventListener {
  private final SessionRegistry sessionRegistry;
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Creates a new {@code WebSocketEventListener}.
   *
   * @param sessionRegistry the registry managing WebSocket sessions
   * @param lobbyService the service handling lobby operations
   * @param messagingTemplate the messaging template used for broadcasting updates
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
   * Handles a WebSocket disconnect event.
   *
   * <p>Removes the disconnected player from the lobby, cleans up session data, and broadcasts the
   * updated player list.
   *
   * @param event the disconnect event containing session information
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
