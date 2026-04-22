package com.codenames.codenames_backend.websocket;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/** Unit tests for {@link WebSocketEventListener}. */
class WebSocketEventListenerTest {
  private SessionRegistry registry;
  private LobbyService lobbyService;
  private SimpMessagingTemplate messagingTemplate;
  private WebSocketEventListener listener;

  @BeforeEach
  void setup() {
    registry = new SessionRegistry();
    lobbyService = mock(LobbyService.class);
    messagingTemplate = mock(SimpMessagingTemplate.class);

    listener = new WebSocketEventListener(registry, lobbyService, messagingTemplate);
  }

  @Test
  void shouldHandleDisconnectAndRemovePlayer() {

    registry.register("123", "Max", "ABCDE");

    SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    when(lobbyService.getPlayers("ABCDE")).thenReturn(java.util.List.of());

    listener.handleDisconnect(event);

    verify(lobbyService).leaveLobby("Max", "ABCDE");
    assertNull(registry.getUser("123"));
  }

  @Test
  void shouldIgnoreDisconnectWhenUsernameIsNull() {

    SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    listener.handleDisconnect(event);

    verifyNoInteractions(lobbyService);
    verifyNoInteractions(messagingTemplate);
  }

  @Test
  void shouldIgnoreDisconnectWhenLobbyIsMissing() {

    registry.register("123", "Max", "ABCDE");
    registry.remove("123");

    SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("123");

    listener.handleDisconnect(event);

    verifyNoInteractions(lobbyService);
    verifyNoInteractions(messagingTemplate);
  }

  @Test
  void shouldIgnoreUnknownSession() {

    SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
    when(event.getSessionId()).thenReturn("unknown");

    listener.handleDisconnect(event);

    verifyNoInteractions(lobbyService);
  }
}
