package com.codenames.codenames_backend.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codenames.codenames_backend.lobby.services.LobbyService;
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

  @BeforeEach
  void setup() {
    lobbyService = mock(LobbyService.class);
    SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
    sessionRegistry = new SessionRegistry();

    controller = new GameController(lobbyService, messagingTemplate, sessionRegistry);
  }

  @Test
  void shouldHandleJoinAndRegisterSession() {

    JoinMessage msg = new JoinMessage();
    msg.setName("Max");
    msg.setCode("ABCDE");

    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
    accessor.setSessionId("123");

    controller.join(msg, accessor);

    verify(lobbyService).joinLobby("Max", "ABCDE");

    assertEquals("Max", sessionRegistry.getUser("123"));
    assertEquals("ABCDE", sessionRegistry.getLobby("123"));
  }
}
