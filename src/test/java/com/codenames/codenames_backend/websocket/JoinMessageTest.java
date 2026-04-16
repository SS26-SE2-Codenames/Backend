package com.codenames.codenames_backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link JoinMessage}. */
public class JoinMessageTest {
  @Test
  void shouldSetAndGetNameAndCode() {
    JoinMessage msg = new JoinMessage();

    msg.setName("Max");
    msg.setCode("ABCDE");

    assertEquals("Max", msg.getName());
    assertEquals("ABCDE", msg.getCode());
  }
}
