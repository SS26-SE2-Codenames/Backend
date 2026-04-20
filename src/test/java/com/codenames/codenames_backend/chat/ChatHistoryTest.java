package com.codenames.codenames_backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatHistoryTest {

  ChatDto message;
  private ChatHistory chatHistory;

  @BeforeEach
  void setUp() {
    chatHistory = new ChatHistory();
    message = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
  }

  @Test
  void testAddLobbyMessage() {
    chatHistory.addLobbyMessage(message);

    assertEquals(1, chatHistory.getLobbyChat().size());
  }

  @Test
  void testAddTeamMessage_red() {
    chatHistory.addTeamMessage("RED", message);

    assertEquals(1, chatHistory.getRedTeamChat().size());
  }

  @Test
  void testAddTeamMessage_blue() {
    chatHistory.addTeamMessage("BLUE", message);

    assertEquals(1, chatHistory.getBlueTeamChat().size());
  }

  @Test
  void testAddMessage_exceedCapacity() {
    for (int i = 1; i <= 51; i++) {
      chatHistory.addLobbyMessage(message);
    }

    assertEquals(50, chatHistory.getLobbyChat().size());
  }

  @Test
  void testAddTeamMessage_invalidTeam() {
    assertThrows(
        IllegalArgumentException.class, () -> chatHistory.addTeamMessage("GREEN", message));
  }
}
