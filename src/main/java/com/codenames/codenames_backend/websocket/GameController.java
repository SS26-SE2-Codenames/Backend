package com.codenames.codenames_backend.websocket;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController {

  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;
  private final SessionRegistry sessionRegistry;

  public GameController(
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate,
      SessionRegistry sessionRegistry) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
    this.sessionRegistry = sessionRegistry;
  }

  @MessageMapping("/join")
  public void join(JoinMessage message, SimpMessageHeaderAccessor headerAccessor) {

    String sessionId = headerAccessor.getSessionId();

    lobbyService.joinLobby(message.getName(), message.getCode());

    sessionRegistry.register(sessionId, message.getName(), message.getCode());

    sendPlayerUpdate(message.getCode());
  }

  private void sendPlayerUpdate(String code) {
    List<String> players =
        lobbyService.getPlayers(code).stream().map(p -> p.getUsername()).toList();

    messagingTemplate.convertAndSend("/topic/lobby/" + code, players);
  }
}
