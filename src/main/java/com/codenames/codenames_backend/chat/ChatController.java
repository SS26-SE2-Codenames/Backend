package com.codenames.codenames_backend.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller for broadcasting client messages to the desired destination with STOMP.
 *
 * <p>The destination is based on the lobbyID or team parameters passed when the method is invoked.
 * The parameters are appended to the destination and broadcasted to all subscribers.
 */
@Slf4j
@Controller
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;
  private final ChatService chatService;

  /**
   * Constructor for the ChatController.
   *
   * @param messagingTemplate the messaging template used for broadcasting updates
   * @param chatService the service used to validate and persist chat history
   */
  public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
    this.messagingTemplate = messagingTemplate;
    this.chatService = chatService;
  }

  /**
   * Sends a message to all players in the same lobby.
   *
   * @param lobbyId the ID of the client who calls the method
   * @param chatDto the message of the client in chatDto format
   */
  @MessageMapping("/chat/{lobbyId}")
  public void sendLobbyMessage(@DestinationVariable String lobbyId, @Payload ChatDto chatDto) {
    try {
      ChatDto validatedMessage = chatService.processLobbyMessage(lobbyId, chatDto);
      messagingTemplate.convertAndSend("/topic/chat/" + lobbyId, validatedMessage);
    } catch (IllegalArgumentException e) {
      log.error("Invalid lobby message: {}", e.getMessage());
    }
  }

  /**
   * Sends a message to all players in the same lobby and on the same team.
   *
   * @param lobbyId the ID of the client who calls the method
   * @param team the team of the client who calls the method
   * @param chatDto the message of the client in chatDto format
   */
  @MessageMapping("/chat/{lobbyId}/{team}")
  public void sendTeamMessage(
      @DestinationVariable String lobbyId,
      @DestinationVariable String team,
      @Payload ChatDto chatDto) {
    try {
      ChatDto validatedMessage = chatService.processTeamMessage(lobbyId, team, chatDto);
      messagingTemplate.convertAndSend("/topic/chat/" + lobbyId + "/" + team, validatedMessage);
    } catch (IllegalArgumentException e) {
      log.error("Invalid team message: {}", e.getMessage());
    }
  }
}
