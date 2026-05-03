package com.codenames.codenames.backend.chat;

import com.codenames.codenames.backend.utility.Team;
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
   * Sends a message to the entire lobby.
   *
   * @param lobbyId the ID of the lobby the client is in
   * @param chatDto the message to be sent
   */
  @MessageMapping("/chat/{lobbyId}")
  public void sendLobbyMessage(@DestinationVariable String lobbyId, @Payload ChatDto chatDto) {
    chatService.processMessage(lobbyId, "LOBBY", "", chatDto);
  }

  /**
   * Sends a message to the entire team.
   *
   * @param lobbyId the ID of the lobby the client is in
   * @param team the team the client is in (RED, BLUE)
   * @param chatDto the message to be sent
   */
  @MessageMapping("/chat/{lobbyId}/{team}")
  public void sendTeamMessage(
      @DestinationVariable String lobbyId,
      @DestinationVariable Team team,
      @Payload ChatDto chatDto) {
    String roomKey = "TEAM_" + team.name();
    String topicSuffix = "/" + team.name();
    chatService.processMessage(lobbyId, roomKey, topicSuffix, chatDto);
  }

  }
}
