package com.codenames.codenames_backend.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Intercepts client messages to server and rebroadcasts to everyone in same room.
 */
@Controller
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;

  public ChatController(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/chat/{lobbyId}")
  public void sendGlobalMessage(@DestinationVariable String lobbyId, @Payload ChatDto chatDto) {
    messagingTemplate.convertAndSend("/topic/chat/" + lobbyId, chatDto);
  }

  @MessageMapping("/chat/{lobbyId}/{team}")
  public void sendTeamMessage(@DestinationVariable String lobbyId, @DestinationVariable String team,
      @Payload ChatDto chatDto) {
    messagingTemplate.convertAndSend("/topic/chat/" + lobbyId + "/" + team, chatDto);
  }
}
