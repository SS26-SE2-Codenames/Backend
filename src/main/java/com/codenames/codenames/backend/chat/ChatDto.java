package com.codenames.codenames.backend.chat;

import com.codenames.codenames.backend.utility.ChatMessageType;

/**
 * Record class for messages that is used in controller.
 *
 * @param senderUsername the username of sender
 * @param content the content of the message
 * @param type the type of message
 */
public record ChatDto(String senderUsername, String content, ChatMessageType type) {}
