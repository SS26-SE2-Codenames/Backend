package com.codenames.codenames.backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.lobby.services.LobbyService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Unit tests for {@link GameController}.
 *
 * <p>Uses mocks to verify interactions with dependencies.
 */
class GameControllerTest {

  private LobbyService lobbyService;
  private SessionRegistry sessionRegistry;
  private GameController controller;
  private SimpMessagingTemplate messagingTemplate;

  @BeforeEach
  void setup() {
    lobbyService = mock(LobbyService.class);
    messagingTemplate = mock(SimpMessagingTemplate.class);
    sessionRegistry = new SessionRegistry();

    controller = new GameController(lobbyService, messagingTemplate, sessionRegistry);
  }

  @Test
  void shouldRegisterJoinAndRegisterSession() {

    JoinMessage msg = new JoinMessage();
    msg.setName("Max");
    msg.setCode("ABCDE");

    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();

    java.util.Map<String, Object> attrs = new java.util.HashMap<>();
    attrs.put("sessionId", "123");

    accessor.setSessionAttributes(attrs);

    when(lobbyService.joinLobby("Max", "ABCDE")).thenReturn(true);

    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of(new Player("Max")));

    controller.join(msg, accessor);

    verify(lobbyService).joinLobby("Max", "ABCDE");

    assertEquals("Max", sessionRegistry.getUser("123"));
    assertEquals("ABCDE", sessionRegistry.getLobby("123"));

    verify(messagingTemplate).convertAndSend(eq("/topic/lobby/ABCDE"), any(Object.class));
  }

  @Test
  void shouldSendErrorMessageWhenJoinFails() {

    JoinMessage msg = new JoinMessage();
    msg.setName("Max");
    msg.setCode("ABCDE");

    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();

    java.util.Map<String, Object> attrs = new java.util.HashMap<>();
    attrs.put("sessionId", "123");
    accessor.setSessionAttributes(attrs);

    when(lobbyService.joinLobby("Max", "ABCDE")).thenReturn(false);

    controller.join(msg, accessor);

    verify(messagingTemplate).convertAndSend("/topic/errors/123", "Join failed");

    verifyNoMoreInteractions(messagingTemplate);
  }

  @Test
  void shouldDoNothingWhenSessionIdIsNull() {

    JoinMessage msg = new JoinMessage();
    msg.setName("Max");
    msg.setCode("ABCDE");

    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();

    controller.join(msg, accessor);

    verifyNoInteractions(lobbyService);
    verifyNoInteractions(messagingTemplate);
  }

  @Test
  void shouldUseSessionAttributesFallbackWhenSessionIdIsNull() {

    JoinMessage msg = new JoinMessage();
    msg.setName("Max");
    msg.setCode("ABCDE");

    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();

    java.util.Map<String, Object> attrs = new java.util.HashMap<>();
    attrs.put("sessionId", "123");
    accessor.setSessionAttributes(attrs);

    when(lobbyService.joinLobby("Max", "ABCDE")).thenReturn(true);
    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of(new Player("Max")));

    controller.join(msg, accessor);

    assertEquals("Max", sessionRegistry.getUser("123"));
    assertEquals("ABCDE", sessionRegistry.getLobby("123"));

    verify(lobbyService).joinLobby("Max", "ABCDE");
    verify(messagingTemplate).convertAndSend(eq("/topic/lobby/ABCDE"), any(Object.class));
  }
}
