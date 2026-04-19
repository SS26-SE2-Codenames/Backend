package com.codenames.codenames_backend.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codenames.codenames_backend.chat.ChatDto.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatDtoTest {

  ChatDto dto;

  @BeforeEach
  void setUp() {
    dto = new ChatDto("TestName", "TestMessage", MessageType.CHAT);
  }

  @Test
  void testConstructor_name() {
    assertEquals("TestName", dto.senderUsername());
  }

  @Test
  void testConstructor_content() {
    assertEquals("TestMessage", dto.content());
  }

  @Test
  void testConstructor_type() {
    assertEquals(MessageType.CHAT, dto.type());
  }

  @Test
  void testConstructor_emptyName() {
    assertThrows(IllegalArgumentException.class, () -> new ChatDto("", "TestMessage", MessageType.CHAT));
  }

  @Test
  void testConstructor_nullContent() {
    assertThrows(IllegalArgumentException.class, () -> new ChatDto("TestName", null, MessageType.CHAT));
  }

  @Test
  void testConstructor_emptyContent() {
    assertThrows(IllegalArgumentException.class, () -> new ChatDto("TestName", "", MessageType.CHAT));
  }

  @Test
  void testConstructor_nullType() {
    assertThrows(IllegalArgumentException.class, () -> new ChatDto("TestName", "TestMessage", null));
  }
}
