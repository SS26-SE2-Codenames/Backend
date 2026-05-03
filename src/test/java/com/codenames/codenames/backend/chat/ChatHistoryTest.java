package com.codenames.codenames.backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codenames.codenames.backend.utility.ChatMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for ChatHistory. */
class ChatHistoryTest {
  private ChatDto message;
  private ChatHistory chatHistory;
  private final String lobbyRoomKey = "LOBBY";
  private final String teamRedRoomKey = "TEAM_RED";
  private final String teamBlueRoomKey = "TEAM_BLUE";

  @BeforeEach
  void setUp() {
    chatHistory = new ChatHistory();
    message = new ChatDto("TestName", "TestMessage", ChatMessageType.CHAT);
  }

  @Test
  void testAddLobbyMessage() {
    chatHistory.addMessage(lobbyRoomKey, message);

    assertEquals(1, chatHistory.getChatLogs().size());
  }

  @Test
  void testAddTeamMessage_red() {
    chatHistory.addMessage(teamRedRoomKey, message);

    assertEquals(1, chatHistory.getChatLogs().get(teamRedRoomKey).size());
  }

  @Test
  void testAddTeamMessage_blue() {
    chatHistory.addMessage(teamBlueRoomKey, message);

    assertEquals(1, chatHistory.getChatLogs().get(teamBlueRoomKey).size());
  }

  @Test
  void testAddTeamMessage_nullRoomKey() {
    assertThrows(IllegalArgumentException.class, () -> chatHistory.addMessage(null, message));
  }

  @Test
  void testAddTeamMessage_blankRoomKey() {
    assertThrows(IllegalArgumentException.class, () -> chatHistory.addMessage("", message));
  }

  @Test
  void testAddMessage_exceedCapacity() {
    for (int i = 1; i <= 51; i++) {
      chatHistory.addMessage(lobbyRoomKey, message);
    }
    assertEquals(50, chatHistory.getChatLogs().get(lobbyRoomKey).size());
  }
}
