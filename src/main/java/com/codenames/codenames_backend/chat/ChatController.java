package com.codenames.codenames_backend.chat;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/** Intercepts client messages to server and rebroadcasts to everyone in same room. */
@Controller
public class ChatController {
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatController(LobbyService lobbyService, SimpMessagingTemplate messagingTemplate) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/chat/{lobbyId}")
  public void sendMessage(@DestinationVariable String lobbyId, @Payload ChatDto chatDto){
    messagingTemplate.convertAndSend("/topic/chat/" + lobbyId, chatDto);
  }
}
