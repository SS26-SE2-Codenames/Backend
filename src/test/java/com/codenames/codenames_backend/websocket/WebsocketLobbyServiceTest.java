package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebsocketLobbyServiceTest {

    private WebsocketLobbyService websocketLobbyService;

    @BeforeEach
    void setup() {
        websocketLobbyService = new WebsocketLobbyService();
    }

    @Test
    void shouldAddPlayerToLobby() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        Player player = new Player("Nati", session);

        websocketLobbyService.addPlayer("ABC", player);

        List<Player> players = websocketLobbyService.getPlayers("ABC");

        assertEquals(1, players.size());
        assertEquals("Nati", players.get(0).getUsername());
    }

    @Test
    void shouldReturnEmptyListIfLobbyDoesNotExist() {
        List<Player> players = websocketLobbyService.getPlayers("UNKNOWN");

        assertNotNull(players);
        assertTrue(players.isEmpty());
    }

    @Test
    void shouldReturnImmutableList() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        Player player = new Player("Nati", session);

        websocketLobbyService.addPlayer("ABC", player);

        List<Player> players = websocketLobbyService.getPlayers("ABC");

        assertThrows(UnsupportedOperationException.class, () -> players.add(player));
    }

    @Test
    void shouldRemovePlayerBySessionId() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        Mockito.when(session.getId()).thenReturn("123");

        Player player = new Player("Nati", session);
        websocketLobbyService.addPlayer("ABC", player);

        websocketLobbyService.removePlayer(session);

        List<Player> players = websocketLobbyService.getPlayers("ABC");

        assertTrue(players.isEmpty());
    }

    @Test
    void shouldAttachSessionToExistingPlayer() {
        WebSocketSession oldSession = Mockito.mock(WebSocketSession.class);
        WebSocketSession newSession = Mockito.mock(WebSocketSession.class);

        Player player = new Player("Nati", oldSession);
        websocketLobbyService.addPlayer("ABC", player);

        websocketLobbyService.attachSession("Nati", "ABC", newSession);

        List<Player> players = websocketLobbyService.getPlayers("ABC");

        assertEquals(newSession, players.get(0).getSession());
    }

    @Test
    void shouldNotFailIfLobbyDoesNotExist() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        websocketLobbyService.attachSession("Nati", "UNKNOWN", session);

        List<Player> players = websocketLobbyService.getPlayers("UNKNOWN");

        assertTrue(players.isEmpty());
    }

    @Test
    void shouldNotChangeAnythingIfPlayerDoesNotExist() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        WebSocketSession originalSession = Mockito.mock(WebSocketSession.class);

        Player player = new Player("Nati", originalSession);
        websocketLobbyService.addPlayer("ABC", player);

        websocketLobbyService.attachSession("OtherUser", "ABC", session);

        List<Player> players = websocketLobbyService.getPlayers("ABC");

        assertEquals(originalSession, players.get(0).getSession());
    }
}
