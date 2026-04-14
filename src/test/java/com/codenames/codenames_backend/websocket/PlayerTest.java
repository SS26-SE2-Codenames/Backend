package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
  @Test
  void shouldReturnUsername() {
    Player player = new Player("Max");

    assertEquals("Max", player.getUsername());
  }
}
