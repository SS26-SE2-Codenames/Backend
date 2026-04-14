package com.codenames.codenames_backend.websocket;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Component
public class WebSocketEventListener {
  private final SessionRegistry sessionRegistry;
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketEventListener(
      SessionRegistry sessionRegistry,
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate) {
    this.sessionRegistry = sessionRegistry;
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {

    String sessionId = event.getSessionId();

    String username = sessionRegistry.getUser(sessionId);
    String lobbyCode = sessionRegistry.getLobby(sessionId);

    if (username == null || lobbyCode == null) return;

    lobbyService.leaveLobby(username, lobbyCode);
    sessionRegistry.remove(sessionId);

    List<String> players =
        lobbyService.getPlayers(lobbyCode).stream().map(Player::getUsername).toList();

    messagingTemplate.convertAndSend("/topic/lobby/" + lobbyCode, players);
  }
}
