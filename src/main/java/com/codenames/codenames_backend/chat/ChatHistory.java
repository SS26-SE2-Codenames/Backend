package com.codenames.codenames_backend.chat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

/**
 * Manages the different message logs for a single lobby, this includes the lobby and both team
 * chats.
 */
@Getter
public class ChatHistory {
  private static final int MAX_MESSAGES = 50;
  private final List<ChatDto> lobbyChat = new CopyOnWriteArrayList<>();
  private final List<ChatDto> redTeamChat = new CopyOnWriteArrayList<>();
  private final List<ChatDto> blueTeamChat = new CopyOnWriteArrayList<>();

  /**
   * Adds a message to the lobby chat history.
   *
   * @param chatDto the message to add
   */
  public void addLobbyMessage(ChatDto chatDto) {
    addMessage(lobbyChat, chatDto);
  }

  /**
   * Adds a message to the team chat history.
   *
   * @param team the color of the team ("RED" or "BLUE")
   * @param chatDto the message to add
   * @throws IllegalArgumentException if the team name is not recognized
   */
  public void addTeamMessage(String team, ChatDto chatDto) {
    if ("RED".equalsIgnoreCase(team)) {
      addMessage(redTeamChat, chatDto);
    } else if ("BLUE".equalsIgnoreCase(team)) {
      addMessage(blueTeamChat, chatDto);
    } else {
      throw new IllegalArgumentException("Unknown Team: " + team);
    }
  }

  /**
   * Helper method to make the limit is not exceeded. This to save memory.
   *
   * @param target the list where the message is to be saved
   * @param chatDto the message to append
   */
  private void addMessage(List<ChatDto> target, ChatDto chatDto) {
    target.add(chatDto);
    if (target.size() > MAX_MESSAGES) {
      target.remove(0);
    }
  }
}
