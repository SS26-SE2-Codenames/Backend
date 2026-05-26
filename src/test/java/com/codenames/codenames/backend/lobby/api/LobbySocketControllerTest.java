package com.codenames.codenames.backend.lobby.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.lobby.api.dto.JoinMessage;
import com.codenames.codenames.backend.lobby.domain.Player;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.serialization.GameStateDataTransferObject;
import com.codenames.codenames.backend.shared.websocket.SessionRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Unit tests for {@link LobbySocketController}.
 *
 * <p>Uses mocks to verify interactions with dependencies.
 */
class LobbySocketControllerTest {

  private LobbyService lobbyService;
  private GameService gameService;
  private SessionRegistry sessionRegistry;
  private LobbySocketController controller;
  private SimpMessagingTemplate messagingTemplate;

  @BeforeEach
  void setup() {
    lobbyService = mock(LobbyService.class);
    gameService = mock(GameService.class);
    messagingTemplate = mock(SimpMessagingTemplate.class);
    sessionRegistry = new SessionRegistry();

    controller =
        new LobbySocketController(lobbyService, gameService, messagingTemplate, sessionRegistry);
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

    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of(new Player("Max", true)));
    when(gameService.getCurrentGameState("ABCDE")).thenReturn(createGameStatePayload());

    controller.join(msg, accessor);

    verify(lobbyService).joinLobby("Max", "ABCDE");

    assertEquals("Max", sessionRegistry.getUser("123"));
    assertEquals("ABCDE", sessionRegistry.getLobby("123"));

    verify(messagingTemplate).convertAndSend(eq("/topic/lobby/ABCDE"), any(Object.class));
    verify(messagingTemplate).convertAndSend(eq("/topic/game/ABCDE"), any(Object.class));
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
    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of());

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
    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of(new Player("Max", true)));
    when(gameService.getCurrentGameState("ABCDE")).thenReturn(createGameStatePayload());

    controller.join(msg, accessor);

    assertEquals("Max", sessionRegistry.getUser("123"));
    assertEquals("ABCDE", sessionRegistry.getLobby("123"));

    verify(lobbyService).joinLobby("Max", "ABCDE");
    verify(messagingTemplate).convertAndSend(eq("/topic/lobby/ABCDE"), any(Object.class));
    verify(messagingTemplate).convertAndSend(eq("/topic/game/ABCDE"), any(Object.class));
  }

  @Test
  void shouldTreatExistingPlayerAsReconnectWhenJoinReturnsFalse() {

    JoinMessage msg = new JoinMessage();
    msg.setName("Max");
    msg.setCode("ABCDE");

    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();

    java.util.Map<String, Object> attrs = new java.util.HashMap<>();
    attrs.put("sessionId", "reconnect-1");
    accessor.setSessionAttributes(attrs);

    when(lobbyService.joinLobby("Max", "ABCDE")).thenReturn(false);
    when(lobbyService.getPlayers("ABCDE")).thenReturn(List.of(new Player("Max", true)));
    when(gameService.getCurrentGameState("ABCDE")).thenReturn(createGameStatePayload());

    controller.join(msg, accessor);

    assertEquals("Max", sessionRegistry.getUser("reconnect-1"));
    assertEquals("ABCDE", sessionRegistry.getLobby("reconnect-1"));

    verify(messagingTemplate).convertAndSend(eq("/topic/lobby/ABCDE"), any(Object.class));
    verify(messagingTemplate).convertAndSend(eq("/topic/game/ABCDE"), any(Object.class));
  }

  private GameStateDataTransferObject createGameStatePayload() {
    return new GameStateDataTransferObject(null, null, null, null, List.of());
  }
}
