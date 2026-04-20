package com.codenames.codenames_backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatServiceTest {

  private final String lobbyId = "TESTLOBBY";
  ChatDto message;
  private ChatService chatService;

  @BeforeEach
  void setUp() {
    chatService = new ChatService();
    message = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
  }

  @Test
  void testNullUsername() {
    assertThrows(IllegalArgumentException.class, () -> chatService.processLobbyMessage(lobbyId, new ChatDto(null, "TestMessage", MessageType.CHAT)));
  }

  @Test
  void testEmptyUsername() {
    assertThrows(IllegalArgumentException.class, () -> chatService.processLobbyMessage(lobbyId, new ChatDto("", "TestMessage", MessageType.CHAT)));
  }

  @Test
  void testNullContent() {
    assertThrows(IllegalArgumentException.class, () -> chatService.processLobbyMessage(lobbyId, new ChatDto("TestName", null, MessageType.CHAT)));
  }

  @Test
  void testEmptyContent() {
    assertThrows(IllegalArgumentException.class, () -> chatService.processLobbyMessage(lobbyId, new ChatDto("TestName", "", MessageType.CHAT)));
  }

  @Test
  void testNullType() {
    assertThrows(IllegalArgumentException.class, () -> chatService.processLobbyMessage(lobbyId, new ChatDto("TestName", "TestMessage", null)));
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