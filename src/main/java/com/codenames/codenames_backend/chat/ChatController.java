package com.codenames.codenames_backend.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller for broadcasting client messages to the desired destination.
 */
@Controller
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Constructor for the ChatController.
   *
   * @param messagingTemplate the messaging template used for broadcasting updates
   */
  public ChatController(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Sends a message to all players in the same lobby.
   *
   * @param lobbyId the ID of the client who calls the method
   * @param chatDto the message of the client in chatDto format
   */
  @MessageMapping("/chat/{lobbyId}")
  public void sendGlobalMessage(@DestinationVariable String lobbyId, @Payload ChatDto chatDto) {
    messagingTemplate.convertAndSend("/topic/chat/" + lobbyId, chatDto);
  }

  /**
   * Sends a message to all players in the same lobby and on the same team.
   *
   * @param lobbyId the ID of the client who calls the method
   * @param team the team of the client who calls the method
   * @param chatDto the message of the client in chatDto format
   */
  @MessageMapping("/chat/{lobbyId}/{team}")
  public void sendTeamMessage(@DestinationVariable String lobbyId, @DestinationVariable String team,
      @Payload ChatDto chatDto) {
    messagingTemplate.convertAndSend("/topic/chat/" + lobbyId + "/" + team, chatDto);
  }
}
