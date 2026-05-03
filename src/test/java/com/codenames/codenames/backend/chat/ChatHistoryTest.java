package com.codenames.codenames.backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codenames.codenames.backend.utility.ChatMessageType;
import com.codenames.codenames.backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for ChatHistory. */
class ChatHistoryTest {
  private ChatDto message;
  private ChatHistory chatHistory;
  private String lobbyRoomKey = "LOBBY";
  private String teamRedRoomKey = "TEAM_RED";
  private String teamBlueRoomKey = "TEAM_BLUE";

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
    chatHistory.addMessage("TEAM_RED", message);

    assertEquals(1, chatHistory.getChatLogs().get("TEAM_RED").size());
  }

  @Test
  void testAddTeamMessage_blue() {
    chatHistory.addMessage("TEAM_BLUE", message);

    assertEquals(1, chatHistory.getChatLogs().get("TEAM_BLUE").size());
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
      chatHistory.addMessage("LOBBY", message);
    }
    assertEquals(50, chatHistory.getChatLogs().get("LOBBY").size());
  }
}
