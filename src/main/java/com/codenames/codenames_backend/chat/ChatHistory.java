package com.codenames.codenames_backend.chat;

import com.codenames.codenames_backend.utility.Team;
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
  public void addTeamMessage(Team team, ChatDto chatDto) {
    if (team == null) {
      throw new IllegalArgumentException("Team cannot be null");
    }
    switch (team) {
      case RED:
        addMessage(redTeamChat, chatDto);
        break;
      case BlUE:
        addMessage(blueTeamChat, chatDto);
        break;
        // google code style accepts switches as exhaustive if all enums are in the switch.
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
