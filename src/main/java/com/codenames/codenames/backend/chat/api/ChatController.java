package com.codenames.codenames.backend.chat.api;

import com.codenames.codenames.backend.chat.api.dto.ChatDto;
import com.codenames.codenames.backend.chat.api.dto.ChatMessageType;
import com.codenames.codenames.backend.chat.application.ChatService;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
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
  private final LobbyService lobbyService;

  /**
   * Constructor for the ChatController.
   *
   * @param chatService the service used to validate and persist chat history
   */
  public ChatController(
      ChatService chatService, LobbyService lobbyService) {
    this.chatService = chatService;
    this.lobbyService = lobbyService;
  }

  /**
   * Sends a message to the entire lobby.
   *
   * @param lobbyId the ID of the lobby the client is in
   * @param chatDto the message to be sent
   */
  @MessageMapping("/chat/{lobbyId}")
  public void sendLobbyMessage(
          @DestinationVariable String lobbyId,
          @Payload ChatDto chatDto) {

    ChatDto verifiedChatDto = new ChatDto(
            chatDto.senderUsername(),
            chatDto.content(),
            ChatMessageType.CHAT
    );
    chatService.processMessage(lobbyId, "LOBBY", "", verifiedChatDto);
  }

  /**
   * Verifies the sender's team and delegates message processing to {@link ChatService}.
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

    Team playerTeam = lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId);
    if (playerTeam != team) {
      throw new IllegalStateException("You are not on team " + team.name());
    }

    ChatDto verifiedDto = new ChatDto(
            chatDto.senderUsername(),
            chatDto.content(),
            ChatMessageType.CHAT
    );

    String roomKey = "TEAM_" + team.name();
    String topicSuffix = "/" + team.name();
    chatService.processMessage(lobbyId, roomKey, topicSuffix, verifiedDto);
  }

  /**
   * Verifies the sender's team and role and delegates message processing to {@link ChatService}.
   *
   * @param lobbyId the ID of the lobby the client is in
   * @param team the team the client is in (RED, BLUE)
   * @param chatDto the message to be sent
   */
  @MessageMapping("/chat/{lobbyId}/{team}/operative")
  public void sendTeamOperativeMessage(
      @DestinationVariable String lobbyId,
      @DestinationVariable Team team,
      @Payload ChatDto chatDto) {

    Team playerTeam = lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId);
    Role playerRole = lobbyService.getPlayerRole(chatDto.senderUsername(), lobbyId);

    if (playerTeam != team || playerRole != Role.OPERATIVE) {
      throw new IllegalStateException(
          "You are either not an operative or are sending to the wrong team.");
    }

    ChatDto verifiedDto = new ChatDto(
            chatDto.senderUsername(),
            chatDto.content(),
            ChatMessageType.CHAT
    );

    String roomKey = "OPERATIVE_" + team.name();
    String topicSuffix = "/" + team.name() + "/operative";
    chatService.processMessage(lobbyId, roomKey, topicSuffix, verifiedDto);
  }
}
