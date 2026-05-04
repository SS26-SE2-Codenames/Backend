package com.codenames.codenames.backend.chat;

import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import com.codenames.codenames.backend.websocket.SessionRegistry;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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
  private final SessionRegistry sessionRegistry;

  /**
   * Constructor for the ChatController.
   *
   * @param chatService the service used to validate and persist chat history
   */
  public ChatController(
      ChatService chatService, LobbyService lobbyService, SessionRegistry sessionRegistry) {
    this.chatService = chatService;
    this.lobbyService = lobbyService;
    this.sessionRegistry = sessionRegistry;
  }

  /**
   * Verifies the username of the sender and checks if they are sending the message to their own
   * lobby before allowing them to send a message.
   *
   * @param headerAccessor the header accessor containing the session information of the sender
   * @param targetLobbyId the actual lobby ID associated with the sender's session, used to verify
   *     that they are sending the message to their own lobby
   * @return the username if sender is verified, otherwise throws an exception
   * @throws IllegalStateException if the username is null or if the sender is trying to send a
   *     message to a lobby they are not in
   */
  private String getVerifiedUsername(
      SimpMessageHeaderAccessor headerAccessor, String targetLobbyId) {
    String sessionId = headerAccessor.getSessionId();
    String realUsername = sessionRegistry.getUser(sessionId);
    String realLobbyId = sessionRegistry.getLobby(sessionId);

    if (realUsername == null) {
      throw new IllegalStateException("Null username.");
    }
    if (targetLobbyId == null) {
      throw new IllegalStateException("Null lobby ID.");
    }
    if (!targetLobbyId.equals(realLobbyId)) {
      throw new IllegalStateException("Wrong lobby, user not in lobby " + realLobbyId);
    }
    return realUsername;
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
      @Payload ChatDto chatDto,
      SimpMessageHeaderAccessor headerAccessor) {
    String realUsername = getVerifiedUsername(headerAccessor, lobbyId);

    // Create a new ChatDto with the verified username to prevent passing false username
    ChatDto verifiedChatDto = new ChatDto(realUsername, chatDto.content(), chatDto.type());
    chatService.processMessage(lobbyId, "LOBBY", "", verifiedChatDto);
  }

  /**
   * Verifies the sender's name, lobbyID, and team before sending a message to the entire team.
   *
   * @param lobbyId the ID of the lobby the client is in
   * @param team the team the client is in (RED, BLUE)
   * @param chatDto the message to be sent
   */
  @MessageMapping("/chat/{lobbyId}/{team}")
  public void sendTeamMessage(
      @DestinationVariable String lobbyId,
      @DestinationVariable Team team,
      @Payload ChatDto chatDto,
      SimpMessageHeaderAccessor headerAccessor) {
    String realUsername = getVerifiedUsername(headerAccessor, lobbyId);

    Team playerTeam = lobbyService.getPlayerTeam(realUsername, lobbyId);
    if (playerTeam != team) {
      throw new IllegalStateException("You are not on team " + team.name());
    }

    ChatDto verifiedDto = new ChatDto(realUsername, chatDto.content(), chatDto.type());

    String roomKey = "TEAM_" + team.name();
    String topicSuffix = "/" + team.name();
    chatService.processMessage(lobbyId, roomKey, topicSuffix, verifiedDto);
  }

  /**
   * Verifies the sender's name, lobbyID, team and role sending a message to the operatives on the
   * respective team.
   *
   * @param lobbyId the ID of the lobby the client is in
   * @param team the team the client is in (RED, BLUE)
   * @param chatDto the message to be sent
   */
  @MessageMapping("/chat/{lobbyId}/{team}/operative")
  public void sendTeamOperativeMessage(
      @DestinationVariable String lobbyId,
      @DestinationVariable Team team,
      @Payload ChatDto chatDto,
      SimpMessageHeaderAccessor headerAccessor) {
    String realUsername = getVerifiedUsername(headerAccessor, lobbyId);

    Team playerTeam = lobbyService.getPlayerTeam(realUsername, lobbyId);
    Role playerRole = lobbyService.getPlayerRole(realUsername, lobbyId);

    if (playerTeam != team || playerRole != Role.OPERATIVE) {
      throw new IllegalStateException(
          "You are either not an operative or are sending to the wrong team.");
    }

    ChatDto verifiedDto = new ChatDto(realUsername, chatDto.content(), chatDto.type());

    String roomKey = "OPERATIVE_" + team.name();
    String topicSuffix = "/" + team.name() + "/operative";
    chatService.processMessage(lobbyId, roomKey, topicSuffix, verifiedDto);
  }
}
