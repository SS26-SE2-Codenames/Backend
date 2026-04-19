package com.codenames.codenames_backend.chat;

import static org.mockito.ArgumentMatchers.eq;
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
  private String lobbyId;
  private String redTeam;
  private String blueTeam;
  private ChatDto chatDto;
  private String globalDestination;
  private String teamDestination;

  @BeforeEach
  void setUp() {
    messagingTemplate = mock(SimpMessagingTemplate.class);
    chatController = new ChatController(messagingTemplate);
    lobbyId = "123";
    redTeam = "RED";
    blueTeam = "BLUE";
    chatDto = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
    globalDestination = "/topic/chat/";
    teamDestination = "/topic/chat/" + lobbyId + "/";
  }

  @Test
  void testSendGlobalMessage() {
    globalDestination += lobbyId;
    chatController.sendGlobalMessage(lobbyId, chatDto);

    verify(messagingTemplate, times(1)).convertAndSend(eq(globalDestination), eq(chatDto));
  }

  @Test
  void testSendTeamMessage_redTeam() {
    teamDestination += redTeam;
    chatController.sendTeamMessage(lobbyId, redTeam, chatDto);

    verify(messagingTemplate, times(1)).convertAndSend(eq(teamDestination), eq(chatDto));
  }@Test
  void testSendTeamMessage_blueTeam() {
    teamDestination += blueTeam;
    chatController.sendTeamMessage(lobbyId, blueTeam, chatDto);

    verify(messagingTemplate, times(1)).convertAndSend(eq(teamDestination), eq(chatDto));
  }
}
