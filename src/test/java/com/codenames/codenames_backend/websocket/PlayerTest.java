package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

  @Test
  void shouldReturnUsernameAndSession() {
    WebSocketSession session = Mockito.mock(WebSocketSession.class);

    Player player = new Player("Nati", session);

    assertEquals("Nati", player.getUsername());
    assertEquals(session, player.getSession());
  }
}
