package com.codenames.codenames_backend.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/** Intercepts client messages to server and rebroadcasts to everyone in same room. */
@Controller
public class ChatController {

  /**
   * Rebroadcast a chat message from an individual to all members sharing the specified room.
   *
   * @param roomId the unique alphanumeric ID for current game lobby
   * @param chatDto the message payload containing sender details and content
   * @return the processed message to be forwarded to topic broker
   */
  @MessageMapping("/chat/{roomId}/sendMessage")
  @SendTo("/topic/room/{roomId}")
  public ChatDto sendMessage(@DestinationVariable String roomId, @Payload ChatDto chatDto) {
    return chatDto;
  }
}
