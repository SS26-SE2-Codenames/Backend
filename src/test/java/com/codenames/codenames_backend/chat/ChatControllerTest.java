package com.codenames.codenames_backend.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** Unit test for Board. */
class ChatControllerTest {
  private SimpMessagingTemplate messagingTemplate;
  private ChatController chatController;
  private ChatService chatService;

  private String lobbyId;
  private String redTeam;
  private String blueTeam;
  private ChatDto chatDto;
  private String lobbyDestination;
  private String teamDestination;

  @BeforeEach
  void setUp() {
    messagingTemplate = mock(SimpMessagingTemplate.class);
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
