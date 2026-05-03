package com.codenames.codenames.backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codenames.codenames.backend.chat.ChatDto.MessageType;
import com.codenames.codenames.backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for ChatHistory. */
class ChatHistoryTest {
  private ChatDto message;
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
    chatHistory.addTeamMessage(Team.RED, message);

    assertEquals(1, chatHistory.getRedTeamChat().size());
  }

  @Test
  void testAddTeamMessage_blue() {
    chatHistory.addTeamMessage(Team.BLUE, message);

    assertEquals(1, chatHistory.getBlueTeamChat().size());
  }

  @Test
  void testAddTeamMessage_nullTeam() {
    assertThrows(IllegalArgumentException.class, () -> chatHistory.addTeamMessage(null, message));
  }

  @Test
  void testAddMessage_exceedCapacity() {
    for (int i = 1; i <= 51; i++) {
      chatHistory.addLobbyMessage(message);
    }
    assertEquals(50, chatHistory.getLobbyChat().size());
  }
}
