package com.codenames.codenames_backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for ChatService. */
class ChatServiceTest {
  private final String lobbyId = "TESTLOBBY";
  ChatDto message;
  private ChatService chatService;

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
    chatService = new ChatService();
    message = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
  }

  @ParameterizedTest(name = "[{index}] Rejects {0}")
  @MethodSource("provideInvalidChatDto")
  void testValidationLogic_invalidMessages(ChatDto invalidMessage) {
    assertThrows(
        IllegalArgumentException.class,
        () -> chatService.processLobbyMessage(lobbyId, invalidMessage));
  }

  @Test
  void testProcessLobbyMessage() {
    ChatDto result = chatService.processLobbyMessage(lobbyId, message);

    assertEquals(message, result);
  }

  @Test
  void testProcessTeamMessage() {
    ChatDto result = chatService.processTeamMessage(lobbyId, "RED", message);

    assertEquals(message, result);
  }

  @Test
  void testClearLobbyHistory() {
    chatService.processLobbyMessage(lobbyId, message);
    chatService.clearLobbyHistory(lobbyId);

    assertThrows(IllegalArgumentException.class, () -> chatService.getChatHistory(lobbyId));
  }

  @Test
  void testGetChatHistory() {
    ChatDto result = chatService.processLobbyMessage(lobbyId, message);

    assertEquals(result, chatService.getChatHistory(lobbyId).getLobbyChat().get(0));
  }

  @Test
  void testGetChatHistory_noMessageYet() {
    assertThrows(IllegalArgumentException.class, () -> chatService.getChatHistory(lobbyId));
  }
}
