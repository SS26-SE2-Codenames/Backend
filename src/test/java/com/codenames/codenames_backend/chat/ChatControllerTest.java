package com.codenames.codenames_backend.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  private String globalDestination;
  private String teamDestination;

  @BeforeEach
  void setUp() {
    messagingTemplate = mock(SimpMessagingTemplate.class);
    chatService = mock(ChatService.class);
    chatController = new ChatController(messagingTemplate, chatService);

    lobbyId = "123";
    redTeam = "RED";
    blueTeam = "BLUE";
    chatDto = new ChatDto("TestName", "TestMessage", MessageType.CHAT);

    globalDestination = "/topic/chat/";
    teamDestination = "/topic/chat/" + lobbyId + "/";
  }

  @Test
  void testSendLobbyMessage() {
    globalDestination += lobbyId;
    when(chatService.processLobbyMessage(lobbyId, chatDto)).thenReturn(chatDto);

    chatController.sendLobbyMessage(lobbyId, chatDto);

    verify(messagingTemplate, times(1)).convertAndSend(globalDestination, chatDto);
  }

  @Test
  void testSendTeamMessage_redTeam() {
    teamDestination += redTeam;
    when(chatService.processTeamMessage(lobbyId, redTeam, chatDto)).thenReturn(chatDto);

    chatController.sendTeamMessage(lobbyId, redTeam, chatDto);

    verify(messagingTemplate, times(1)).convertAndSend(teamDestination, chatDto);
  }

  @Test
  void testSendTeamMessage_blueTeam() {
    teamDestination += blueTeam;
    when(chatService.processTeamMessage(lobbyId, blueTeam, chatDto)).thenReturn(chatDto);

    chatController.sendTeamMessage(lobbyId, blueTeam, chatDto);

    verify(messagingTemplate, times(1)).convertAndSend(teamDestination, chatDto);
  }
}
