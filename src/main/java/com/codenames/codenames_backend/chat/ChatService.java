package com.codenames.codenames_backend.chat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatService {
  // Each lobby has their own list of ChatDto.
  private final Map<String, List<ChatDto>> lobbyChatHistory = new ConcurrentHashMap<>();

  private static final int MAX_MESSAGES = 50;

  public ChatDto parsingAndStoringMessage(String lobbyId, ChatDto chatDto){
    if (chatDto.senderUsername() == null || chatDto.senderUsername().isEmpty()) {
      throw new IllegalArgumentException("Sender username cannot be null or empty");
    }
    if (chatDto.content() == null || chatDto.content().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be null or empty");
    }
    if (chatDto.type() == null) {
      throw new IllegalArgumentException("Please specify chat type");
    }

    return chatDto;
  }
}
