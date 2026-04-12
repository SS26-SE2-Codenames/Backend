package com.codenames.codenames_backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

class GameWebSocketHandlerTest {

    private WebsocketLobbyService websocketLobbyService;
    private GameWebSocketHandler handler;

    @BeforeEach
    void setup() {
        websocketLobbyService = Mockito.spy(new WebsocketLobbyService());
        handler = new GameWebSocketHandler(websocketLobbyService);
    }

    @Test
    void shouldHandleJoinAndBroadcastPlayerList() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("1");

        ObjectMapper mapper = new ObjectMapper();
        String payload =
                mapper.writeValueAsString(
                        Map.of(
                                "type", "JOIN",
                                "name", "Nati",
                                "code", "ABC"));

        handler.handleTextMessage(session, new TextMessage(payload));

        assertEquals(1, websocketLobbyService.getPlayers("ABC").size());
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
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
