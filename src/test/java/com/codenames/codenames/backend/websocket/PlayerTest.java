package com.codenames.codenames.backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link Player}. */
class PlayerTest {
  @Test
  void shouldReturnUsername() {
    Player player = new Player("Max");

    assertEquals("Max", player.getUsername());
  }
}
