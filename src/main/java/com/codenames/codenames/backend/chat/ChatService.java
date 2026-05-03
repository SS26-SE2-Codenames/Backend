package com.codenames.codenames.backend.chat;

import com.codenames.codenames.backend.utility.Team;
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
  // Each lobby now stores 3 COWAL
  private final Map<String, ChatHistory> lobbyChatHistory = new ConcurrentHashMap<>();

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
   * Validates and saves a message fin the lobby chat history logs.
   *
   * @param lobbyId the ID of the lobby
   * @param chatDto the message to process and store
   * @throws IllegalArgumentException if validation fails
   */
  public void processLobbyMessage(String lobbyId, ChatDto chatDto) {
    try {
      validateDto(chatDto);

      ChatHistory currentLobbyChat =
          lobbyChatHistory.computeIfAbsent(lobbyId, k -> new ChatHistory());
      currentLobbyChat.addLobbyMessage(chatDto);
      messagingTemplate.convertAndSend("/topic/chat/" + lobbyId, chatDto);
    } catch (IllegalArgumentException e) {
      log.error("Invalid lobby message: {}", e.getMessage());
    }
  }

  /**
   * Validates and saves a message fin the team chat history logs.
   *
   * @param lobbyId the ID of the lobby
   * @param team the color of the team ("RED" or "BLUE")
   * @param chatDto the message to process and store
   * @throws IllegalArgumentException if validation fails
   */
  public void processTeamMessage(String lobbyId, Team team, ChatDto chatDto) {
    try {
      validateDto(chatDto);

      ChatHistory currentTeamChat =
          lobbyChatHistory.computeIfAbsent(lobbyId, k -> new ChatHistory());
      currentTeamChat.addTeamMessage(team, chatDto);
      messagingTemplate.convertAndSend("/topic/chat/" + lobbyId + "/" + team.name(), chatDto);
    } catch (IllegalArgumentException e) {
      log.error("Invalid team message: {}", e.getMessage());
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
