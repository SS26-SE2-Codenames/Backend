package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

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
  void shouldHandleJoinAndBroadcastPlayerList() throws IOException {
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
    verify(session).sendMessage(any(TextMessage.class));
  }

  @Test
  void shouldIgnoreNonJoinMessages() throws IOException {
    WebSocketSession session = mock(WebSocketSession.class);

    String payload =
        """
      {
        "type": "LEAVE"
      }
      """;

    handler.handleTextMessage(session, new TextMessage(payload));

    // verify that nothing was sent
    verify(session, never()).sendMessage(any());
  }

  @Test
  void shouldIgnoreInvalidJson() throws IOException {
    WebSocketSession session = mock(WebSocketSession.class);

    handler.handleTextMessage(session, new TextMessage("{}"));

    verify(session, never()).sendMessage(any());
  }
}
