package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SessionRegistryTest {

  @Test
  void shouldRegisterAndRetrieveSession() {
    SessionRegistry registry = new SessionRegistry();

    registry.register("123", "Max", "ABCDE");

    assertEquals("Max", registry.getUser("123"));
    assertEquals("ABCDE", registry.getLobby("123"));
  }

  @Test
  void shouldRemoveSession() {
    SessionRegistry registry = new SessionRegistry();

    registry.register("123", "Max", "ABCDE");
    registry.remove("123");

    assertNull(registry.getUser("123"));
    assertNull(registry.getLobby("123"));
  }
}
