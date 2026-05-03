package com.codenames.codenames.backend.chat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codenames.codenames.backend.utility.ChatMessageType;
import com.codenames.codenames.backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for ChatController. */
class ChatControllerTest {
  private ChatController chatController;
  private ChatService chatService;

  private String lobbyId;
  private Team redTeam;
  private Team blueTeam;
  private ChatDto chatDto;

  @BeforeEach
  void setUp() {
    chatService = mock(ChatService.class);
    chatController = new ChatController(chatService);

    lobbyId = "123";
    chatDto = new ChatDto("TestName", "TestMessage", ChatMessageType.CHAT);

    redTeam = Team.RED;
    blueTeam = Team.BLUE;
  }

  @Test
  void testSendLobbyMessage() {
    chatController.sendLobbyMessage(lobbyId, chatDto);

    verify(chatService, times(1)).processMessage(lobbyId, "LOBBY", "", chatDto);
  }

  @Test
  void testSendTeamMessage_redTeam() {
    chatController.sendTeamMessage(lobbyId, redTeam, chatDto);

    verify(chatService, times(1)).processMessage(lobbyId, "TEAM_RED", "/RED", chatDto);
  }

  @Test
  void testSendTeamOperativeMessage_blueTeam() {
    chatController.sendTeamOperativeMessage(lobbyId, blueTeam, chatDto);

    verify(chatService, times(1))
        .processMessage(lobbyId, "OPERATIVE_BLUE", "/BLUE/operative", chatDto);
  }
}
