package com.codenames.codenames.backend.shared.websocket;

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
  private static final String TEST_SESSION_ID = "123";
  private SessionRegistry registry;
  private WebSocketEventListener listener;

  @BeforeEach
  void setup() {
    registry = new SessionRegistry();
    listener = new WebSocketEventListener(registry);
  }

  @Test
  void shouldHandleDisconnectAndRemoveSessionMapping() {

    registry.register(TEST_SESSION_ID, "Max", "ABCDE");

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn(TEST_SESSION_ID);

    listener.handleDisconnect(event);

    assertNull(registry.getUser(TEST_SESSION_ID));
    assertNull(registry.getLobby(TEST_SESSION_ID));
  }

  @Test
  void shouldIgnoreDisconnectWhenUsernameIsNull() {

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn(TEST_SESSION_ID);

    listener.handleDisconnect(event);

    assertNull(registry.getUser(TEST_SESSION_ID));
  }

  @Test
  void shouldIgnoreDisconnectWhenLobbyIsMissing() {

    registry.register(TEST_SESSION_ID, "Max", "ABCDE");
    registry.remove(TEST_SESSION_ID);

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn(TEST_SESSION_ID);

    listener.handleDisconnect(event);
  }

  @Test
  void shouldIgnoreUnknownSession() {

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("unknown");

    listener.handleDisconnect(event);

    assertNull(registry.getUser(TEST_SESSION_ID));
  }

  @Test
  void shouldIgnoreDisconnectWhenLobbyIsNullButUserExists() throws Exception {

    registry.register(TEST_SESSION_ID, "Max", "ABCDE");
    removeLobbyMappingForTestSession();

    SessionDisconnectEvent event = org.mockito.Mockito.mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn(TEST_SESSION_ID);

    listener.handleDisconnect(event);

    assertEquals("Max", registry.getUser(TEST_SESSION_ID));
    assertNull(registry.getLobby(TEST_SESSION_ID));
  }

  @SuppressWarnings("unchecked")
  private void removeLobbyMappingForTestSession() throws Exception {
    Field lobbyField = SessionRegistry.class.getDeclaredField("sessionToLobby");
    lobbyField.setAccessible(true);

    Map<String, String> sessionToLobby = (Map<String, String>) lobbyField.get(registry);
    sessionToLobby.remove(TEST_SESSION_ID);

    assertNull(registry.getLobby(TEST_SESSION_ID));
  }
}
