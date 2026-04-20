package com.codenames.codenames_backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames_backend.lobby.services.LobbyService;
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
    accessor.setSessionAttributes(new java.util.HashMap<>());
    accessor.getSessionAttributes().put("sessionId", "123");

    when(lobbyService.joinLobby("Max", "ABCDE")).thenReturn(true);

    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of(new Player("Max")));

    controller.join(msg, accessor);

    verify(lobbyService).joinLobby("Max", "ABCDE");

    assertEquals("Max", sessionRegistry.getUser("123"));
    assertEquals("ABCDE", sessionRegistry.getLobby("123"));

    verify(messagingTemplate).convertAndSend(eq("/topic/lobby/ABCDE"), any(Object.class));
  }
}
