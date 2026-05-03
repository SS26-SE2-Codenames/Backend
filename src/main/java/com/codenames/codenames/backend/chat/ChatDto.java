package com.codenames.codenames.backend.chat;

/**
 * Record class for messages that is used in controller.
 *
 * @param senderUsername the username of sender
 * @param content the content of the message
 * @param type the type of message
 */
public record ChatDto(String senderUsername, String content, MessageType type) {

  /** Various message types that can be used in the game. */
  public enum MessageType {
    CHAT
  }
}
