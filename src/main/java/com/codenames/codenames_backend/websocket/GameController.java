package com.codenames.codenames_backend.websocket;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController {

  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  public GameController(LobbyService lobbyService, SimpMessagingTemplate messagingTemplate) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/join")
  public void join(JoinMessage message) {

    lobbyService.joinLobby(message.getName(), message.getCode());

    List<String> players =
        lobbyService.getPlayers(message.getCode()).stream().map(p -> p.getUsername()).toList();
    messagingTemplate.convertAndSend("/topic/lobby/" + message.getCode(), players);
  }
}
