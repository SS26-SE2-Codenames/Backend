package com.codenames.codenames_backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link Player}. */
public class PlayerTest {
  @Test
  void shouldReturnUsername() {
    Player player = new Player("Max");

    assertEquals("Max", player.getUsername());
  }
}
