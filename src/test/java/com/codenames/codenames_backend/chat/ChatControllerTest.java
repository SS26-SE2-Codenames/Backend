package com.codenames.codenames_backend.chat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import com.codenames.codenames_backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for Board. */
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
    chatDto = new ChatDto("TestName", "TestMessage", MessageType.CHAT);

    redTeam = Team.RED;
    blueTeam = Team.BlUE;
  }

  @Test
  void testSendLobbyMessage() {
    chatController.sendLobbyMessage(lobbyId, chatDto);

    verify(chatService, times(1)).processLobbyMessage(lobbyId, chatDto);
  }

  @Test
  void testSendTeamMessage_redTeam() {
    chatController.sendTeamMessage(lobbyId, redTeam, chatDto);

    verify(chatService, times(1)).processTeamMessage(lobbyId, redTeam, chatDto);
  }

  @Test
  void testSendTeamMessage_blueTeam() {
    chatController.sendTeamMessage(lobbyId, blueTeam, chatDto);

    verify(chatService, times(1)).processTeamMessage(lobbyId, blueTeam, chatDto);
  }
}
