package com.codenames.codenames_backend.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
  // Each lobby now stores 3 COWAL
  private final Map<String, ChatHistory> lobbyChatHistory = new ConcurrentHashMap<>();

  private static void validateDto(ChatDto chatDto) {
    if (chatDto.senderUsername() == null || chatDto.senderUsername().isEmpty()) {
      throw new IllegalArgumentException("Sender username cannot be null or empty");
    }
    if (chatDto.content() == null || chatDto.content().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be null or empty");
    }
    if (chatDto.type() == null) {
      throw new IllegalArgumentException("Please specify chat type");
    }
  }

  public ChatDto processLobbyMessage(String lobbyId, ChatDto chatDto) {
    validateDto(chatDto);

    ChatHistory currentLobbyChat =
        lobbyChatHistory.computeIfAbsent(lobbyId, (k) -> new ChatHistory());
    currentLobbyChat.addLobbyMessage(chatDto);

    return chatDto;
  }

  public ChatDto processTeamMessage(String lobbyId, String team, ChatDto chatDto) {
    validateDto(chatDto);

    ChatHistory currentTeamChat =
        lobbyChatHistory.computeIfAbsent(lobbyId, (k) -> new ChatHistory());
    currentTeamChat.addTeamMessage(team, chatDto);

    return chatDto;
  }

  public void clearLobbyHistory(String lobbyId) {
    lobbyChatHistory.remove(lobbyId);
  }
}
