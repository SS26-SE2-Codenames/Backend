package com.codenames.codenames_backend.chat;

public record ChatDTO(String senderUsername, String content, MessageType type) {

  public ChatDTO {
    if (senderUsername == null || senderUsername.isEmpty()) {
      throw new IllegalArgumentException("Sender username cannot be null or empty");
    }
    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("Content cannot be null or empty");
    }
    if (type == null) {
      throw new IllegalArgumentException("Please specify chat type");
    }
  }

  enum MessageType {
    CHAT,
    SERVERINFO
  }
}
