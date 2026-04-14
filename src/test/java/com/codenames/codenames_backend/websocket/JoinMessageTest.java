package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
