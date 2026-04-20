package com.codenames.codenames_backend.chat;

import com.codenames.codenames_backend.utility.Team;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Controller for broadcasting client messages to the desired destination with STOMP.
 *
 * <p>The destination is based on the lobbyID or team parameters passed when the method is invoked.
 * The parameters are appended to the destination and broadcasted to all subscribers.
 */
@Controller
public class ChatController {

  private final ChatService chatService;

  /**
   * Constructor for the ChatController.
   *
   * @param chatService the service used to validate and persist chat history
   */
  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  /**
   * Sends a message to all players in the same lobby.
   *
   * @param lobbyId the ID of where the message is broadcasted to
   * @param chatDto the message of the client in chatDto format
   */
  @MessageMapping("/chat/{lobbyId}")
  public void sendLobbyMessage(@DestinationVariable String lobbyId, @Payload ChatDto chatDto) {
    chatService.processLobbyMessage(lobbyId, chatDto);
  }

  /**
   * Sends a message to all players in the same lobby and on the same team.
   *
   * @param lobbyId the ID of where the message is broadcasted to
   * @param team the team of the client who calls the method
   * @param chatDto the message of the client in chatDto format
   */
  @MessageMapping("/chat/{lobbyId}/{team}")
  public void sendTeamMessage(
      @DestinationVariable String lobbyId,
      @DestinationVariable Team team,
      @Payload ChatDto chatDto) {
    chatService.processTeamMessage(lobbyId, team, chatDto);
  }
}
