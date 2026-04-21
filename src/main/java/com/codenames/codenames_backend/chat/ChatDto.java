package com.codenames.codenames_backend.chat;

/**
 * Record class for messages that is used in controller.
 *
 * @param senderUsername the username of sender
 * @param content the content of the message
 * @param type the type of message
 */
public record ChatDto(String senderUsername, String content, MessageType type) {

  /**
   * Constructor to check for null or empty messages.
   *
   * @param senderUsername the username of sender
   * @param content the content of the message
   * @param type the type of message
   */
  public ChatDto {
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

  /** Various message types that can be used in the game. */
  public enum MessageType {
    CHAT
  }
}
