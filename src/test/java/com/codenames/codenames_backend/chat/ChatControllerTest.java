package com.codenames.codenames_backend.chat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for Board. */
class ChatControllerTest {
  private ChatController chatController;
  private ChatService chatService;

  private String lobbyId;
  private String redTeam;
  private String blueTeam;
  private ChatDto chatDto;

  @BeforeEach
  void setUp() {
    chatService = mock(ChatService.class);
    chatController = new ChatController(chatService);

    lobbyId = "123";
    redTeam = "RED";
    blueTeam = "BLUE";
    chatDto = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
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
