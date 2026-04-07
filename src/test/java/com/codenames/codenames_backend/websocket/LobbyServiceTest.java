package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {

  private LobbyService lobbyService;

  @BeforeEach
  void setup() {
    lobbyService = new LobbyService();
  }

  @Test
  void shouldAddPlayerToLobby() {
    WebSocketSession session = Mockito.mock(WebSocketSession.class);
    Player player = new Player("Nati", session);

    lobbyService.addPlayer("ABC", player);

    List<Player> players = lobbyService.getPlayers("ABC");

    assertEquals(1, players.size());
    assertEquals("Nati", players.get(0).getUsername());
  }

  @Test
  void shouldReturnEmptyListIfLobbyDoesNotExist() {
    List<Player> players = lobbyService.getPlayers("UNKNOWN");

    assertNotNull(players);
    assertTrue(players.isEmpty());
  }

  @Test
  void shouldReturnImmutableList() {
    WebSocketSession session = Mockito.mock(WebSocketSession.class);
    Player player = new Player("Nati", session);

    lobbyService.addPlayer("ABC", player);

    List<Player> players = lobbyService.getPlayers("ABC");

    assertThrows(UnsupportedOperationException.class, () -> players.add(player));
  }

  @Test
  void shouldRemovePlayerBySessionId() {
    WebSocketSession session = Mockito.mock(WebSocketSession.class);
    Mockito.when(session.getId()).thenReturn("123");

    Player player = new Player("Nati", session);
    lobbyService.addPlayer("ABC", player);

    lobbyService.removePlayer(session);

    List<Player> players = lobbyService.getPlayers("ABC");

    assertTrue(players.isEmpty());
  }
}
