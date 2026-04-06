package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameWebSocketHandlerTest {

  private LobbyService lobbyService;
  private GameWebSocketHandler handler;

  @BeforeEach
  void setup() {
    lobbyService = Mockito.spy(new LobbyService());
    handler = new GameWebSocketHandler(lobbyService);
  }

  @Test
  void shouldHandleJoinAndBroadcastPlayerList() throws Exception {
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.getId()).thenReturn("1");

    String payload =
        """
        {
          "type": "JOIN",
          "name": "Nati",
          "code": "ABC"
        }
        """;

    handler.handleTextMessage(session, new TextMessage(payload));

    // verify player added
    assertEquals(1, lobbyService.getPlayers("ABC").size());

    // verify message sent
    verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
  }
}
