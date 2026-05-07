package com.codenames.codenames.backend.chat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

/** Manages the different message logs for a lobby. */
@Getter
public class ChatHistory {
  private static final int MAX_MESSAGES = 50;

  private final Map<String, List<ChatDto>> chatLogs = new ConcurrentHashMap<>();

  /**
   * Adds a message to the specified chat room history.
   *
   * @param roomKey the unique identifier for the chat room (lobby, team color, role)
   * @param chatDto the message to add
   */
  public void addMessage(String roomKey, ChatDto chatDto) {
    if (roomKey == null || roomKey.isBlank()) {
      throw new IllegalArgumentException("Room key cannot be null or empty");
    }

    List<ChatDto> targetLog = chatLogs.computeIfAbsent(roomKey, k -> new CopyOnWriteArrayList<>());

    targetLog.add(chatDto);
    if (targetLog.size() > MAX_MESSAGES) {
      targetLog.remove(0);
    }
  }
}
