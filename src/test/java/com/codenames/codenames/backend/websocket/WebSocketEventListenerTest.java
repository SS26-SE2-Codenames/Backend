package com.codenames.codenames.backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/** Unit tests for {@link WebSocketEventListener}. */
class WebSocketEventListenerTest {
  private SessionRegistry registry;
  private WebSocketEventListener listener;

  @BeforeEach
  void setup() {
    registry = new SessionRegistry();
    listener = new WebSocketEventListener(registry);
  }

  @Test
  void shouldHandleDisconnectAndRemoveSessionMapping() {

    registry.register("123", "Max", "ABCDE");

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    listener.handleDisconnect(event);

    assertNull(registry.getUser("123"));
    assertNull(registry.getLobby("123"));
  }

  @Test
  void shouldIgnoreDisconnectWhenUsernameIsNull() {

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    listener.handleDisconnect(event);
  }

  @Test
  void shouldIgnoreDisconnectWhenLobbyIsMissing() {

    registry.register("123", "Max", "ABCDE");
    registry.remove("123");

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    listener.handleDisconnect(event);
  }

  @Test
  void shouldIgnoreUnknownSession() {

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("unknown");

    listener.handleDisconnect(event);
  }

  @Test
  void shouldIgnoreDisconnectWhenLobbyIsNullButUserExists() throws Exception {

    registry.register("123", "Max", "ABCDE");
    removeLobbyMappingOnly("123");

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    listener.handleDisconnect(event);

    assertEquals("Max", registry.getUser("123"));
    assertNull(registry.getLobby("123"));
  }

  @SuppressWarnings("unchecked")
  private void removeLobbyMappingOnly(String sessionId) throws Exception {
    Field lobbyField = SessionRegistry.class.getDeclaredField("sessionToLobby");
    lobbyField.setAccessible(true);

    Map<String, String> sessionToLobby = (Map<String, String>) lobbyField.get(registry);
    sessionToLobby.remove(sessionId);
  }
}
