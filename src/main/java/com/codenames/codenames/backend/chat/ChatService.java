package com.codenames.codenames.backend.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/** Service responsible for processing, validating and storing messages. */
@Slf4j
@Service
public class ChatService {
  private final SimpMessagingTemplate messagingTemplate;
  // Contains all chat history for all lobbies
  private final Map<String, ChatHistory> lobbyChatHistory = new ConcurrentHashMap<>();

  /**
   * Constructor for ChatService.
   *
   * @param messagingTemplate the template for broadcasting
   */
  public ChatService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

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

  /**
   * Validates, stores, and broadcasts a message to a specific topic.
   *
   * @param lobbyId the ID of the lobby for map across all lobbies
   * @param roomKey the internal key used to store history (e.g., "LOBBY", "TEAM_RED") per lobby
   * @param destinationTopic the specific STOMP topic suffix to broadcast to
   * @param chatDto the message to process and store
   * @throws IllegalArgumentException invalid chatDto or when lobbyId/roomKey are null or empty
   */
  public void processMessage(
      String lobbyId, String roomKey, String destinationTopic, ChatDto chatDto) {
    try {
      validateDto(chatDto);

      if (lobbyId == null || lobbyId.trim().isEmpty()) {
        log.error("Attempted to process message for invalid lobbyId.");
        throw new IllegalArgumentException("Lobby ID cannot be null or empty");
      }

      if (roomKey == null || roomKey.trim().isEmpty()) {
        log.error("Invalid roomKey: null or empty");
        throw new IllegalArgumentException("roomKey cannot be null or empty");
      }

      ChatHistory currentLobbyChat =
          lobbyChatHistory.computeIfAbsent(lobbyId, k -> new ChatHistory());

      currentLobbyChat.addMessage(roomKey, chatDto);

      messagingTemplate.convertAndSend("/topic/chat/" + lobbyId + destinationTopic, chatDto);
    } catch (IllegalArgumentException e) {
      log.error("Invalid chat message for room {}: {}", roomKey, e.getMessage());
    }
  }

  /**
   * Deletes all chat history when lobby is deleted.
   *
   * @param lobbyId the ID of the lobby to delete chat logs of
   */
  public void clearLobbyHistory(String lobbyId) {
    lobbyChatHistory.remove(lobbyId);
  }

  /**
   * Retrieves the chat history log for a specific lobby.
   *
   * @param lobbyId the ID of the lobby
   * @return the ChatHistory of the lobby
   * @throws IllegalArgumentException if no history exists for the given ID
   */
  public ChatHistory getChatHistory(String lobbyId) {
    ChatHistory history = lobbyChatHistory.get(lobbyId);
    if (history == null) {
      throw new IllegalArgumentException("No lobby found with id: " + lobbyId);
    }
    return history;
  }
}
