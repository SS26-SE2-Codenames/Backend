package com.codenames.codenames.backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codenames.codenames.backend.utility.ChatMessageType;
import com.codenames.codenames.backend.utility.Team;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** Unit tests for ChatService. */
class ChatServiceTest {
  ChatDto message;
  private String lobbyId;
  private Team team;
  private ChatService chatService;
  private SimpMessagingTemplate messagingTemplate;
  private final String lobbyRoomKey = "LOBBY";
  private final String teamRoomKey = "TEAM_";

  private static Stream<Arguments> provideInvalidChatDto() {
    return Stream.of(
        arguments(new ChatDto(null, "TestMessage", ChatMessageType.CHAT)),
        arguments(new ChatDto("", "TestMessage", ChatMessageType.CHAT)),
        arguments(new ChatDto("TestName", null, ChatMessageType.CHAT)),
        arguments(new ChatDto("TestName", "", ChatMessageType.CHAT)),
        arguments(new ChatDto("TestName", "TestMessage", null)));
  }

  @BeforeEach
  void setUp() {
    lobbyId = "123";
    team = Team.RED;
    messagingTemplate = mock(SimpMessagingTemplate.class);
    chatService = new ChatService(messagingTemplate);
    message = new ChatDto("TestName", "TestMessage", ChatMessageType.CHAT);
  }

  @ParameterizedTest
  @MethodSource("provideInvalidChatDto")
  void testProcessMessage_invalidMessages(ChatDto invalidMessage) {
    chatService.processMessage(lobbyId, lobbyRoomKey, "", invalidMessage);

    verify(messagingTemplate, never()).convertAndSend("/topic/chat/" + lobbyId, message);
  }

  @Test
  void testProcessLobbyMessage() {
    chatService.processMessage(lobbyId, lobbyRoomKey, "", message);

    verify(messagingTemplate, times(1)).convertAndSend("/topic/chat/" + lobbyId, message);
  }

  @Test
  void testProcessTeamMessage() {
    chatService.processMessage(lobbyId, teamRoomKey + team.name(), "/" + team.name(), message);

    verify(messagingTemplate, times(1))
        .convertAndSend("/topic/chat/" + lobbyId + "/" + team.name(), message);
  }

  @Test
  void testClearLobbyHistory() {
    chatService.processMessage(lobbyId, lobbyRoomKey, "", message);
    chatService.clearLobbyHistory(lobbyId);

    assertThrows(IllegalArgumentException.class, () -> chatService.getChatHistory(lobbyId));
  }

  @Test
  void testGetChatHistory() {
    chatService.processMessage(lobbyId, lobbyRoomKey, "", message);

    assertEquals(message, chatService.getChatHistory(lobbyId).getChatLogs().get("LOBBY").get(0));
  }

  @Test
  void testGetChatHistory_noMessageYet() {
    assertThrows(IllegalArgumentException.class, () -> chatService.getChatHistory(lobbyId));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void testProcessMessage_invalidLobbyId(String input) {
    chatService.processMessage(input, lobbyRoomKey, "", message);
    verify(messagingTemplate, never()).convertAndSend("/topic/chat/" + lobbyId, message);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void testProcessMessage_invalidRoomKey(String input) {
    chatService.processMessage(lobbyId, input, "", message);
    verify(messagingTemplate, never()).convertAndSend("/topic/chat/" + lobbyId, message);
  }

}
