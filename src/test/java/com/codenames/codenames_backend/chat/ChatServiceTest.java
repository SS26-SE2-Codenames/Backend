package com.codenames.codenames_backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import com.codenames.codenames_backend.utility.Team;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** Unit tests for ChatService. */
class ChatServiceTest {
  ChatDto message;
  private String lobbyId;
  private Team team;
  private ChatService chatService;
  private SimpMessagingTemplate messagingTemplate;

  private static Stream<Arguments> provideInvalidChatDto() {
    return Stream.of(
        arguments(new ChatDto(null, "TestMessage", MessageType.CHAT)),
        arguments(new ChatDto("", "TestMessage", MessageType.CHAT)),
        arguments(new ChatDto("TestName", null, MessageType.CHAT)),
        arguments(new ChatDto("TestName", "", MessageType.CHAT)),
        arguments(new ChatDto("TestName", "TestMessage", null)));
  }

  @BeforeEach
  void setUp() {
    lobbyId = "TESTLOBBY";
    team = Team.RED;
    messagingTemplate = mock(SimpMessagingTemplate.class);
    chatService = new ChatService(messagingTemplate);
    message = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
  }

  @ParameterizedTest(name = "[{index}] Rejects {0}")
  @MethodSource("provideInvalidChatDto")
  void testLobbyValidationLogic_invalidMessages(ChatDto invalidMessage) {
    chatService.processLobbyMessage(lobbyId, invalidMessage);

    verify(messagingTemplate, never()).convertAndSend("/topic/chat/" + lobbyId, message);
  }

  @ParameterizedTest(name = "[{index}] Rejects {0}")
  @MethodSource("provideInvalidChatDto")
  void testTeamValidationLogic_invalidMessages(ChatDto invalidMessage) {
    chatService.processTeamMessage(lobbyId, team, invalidMessage);

    verify(messagingTemplate, never())
        .convertAndSend("/topic/chat/" + lobbyId + "/" + team.name(), message);
  }

  @Test
  void testProcessLobbyMessage() {
    chatService.processLobbyMessage(lobbyId, message);

    verify(messagingTemplate, times(1)).convertAndSend("/topic/chat/" + lobbyId, message);
  }

  @Test
  void testProcessTeamMessage() {
    chatService.processTeamMessage(lobbyId, team, message);

    verify(messagingTemplate, times(1))
        .convertAndSend("/topic/chat/" + lobbyId + "/" + team.name(), message);
  }

  @Test
  void testClearLobbyHistory() {
    chatService.processLobbyMessage(lobbyId, message);
    chatService.clearLobbyHistory(lobbyId);

    assertThrows(IllegalArgumentException.class, () -> chatService.getChatHistory(lobbyId));
  }

  @Test
  void testGetChatHistory() {
    chatService.processLobbyMessage(lobbyId, message);

    assertEquals(message, chatService.getChatHistory(lobbyId).getLobbyChat().get(0));
  }

  @Test
  void testGetChatHistory_noMessageYet() {
    assertThrows(IllegalArgumentException.class, () -> chatService.getChatHistory(lobbyId));
  }
}
