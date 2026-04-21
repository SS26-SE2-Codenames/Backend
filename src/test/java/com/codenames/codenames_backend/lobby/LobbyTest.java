package com.codenames.codenames_backend.lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Lobby}.
 *
 * <p>Validates player management and lobby constraints.
 */
class LobbyTest {

  @Test
  void constructorShouldInitializeLobbyCorrectly() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    assertEquals("ABCDE", lobby.getLobbyCode());
    assertEquals(1, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.getUsername().equals("Host")));
  }

  @Test
  void addPlayerShouldAddPlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1");

    assertEquals(2, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.getUsername().equals("P1")));
  }

  @Test
  void addPlayerShouldNotExceedMaxPlayers() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1");
    lobby.addPlayer("P2");
    lobby.addPlayer("P3");
    lobby.addPlayer("P4"); // sollte ignoriert werden

    assertEquals(4, lobby.getPlayerList().size());
  }

  @Test
  void removePlayerShouldRemovePlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1");
    lobby.removePlayer("P1");

    assertFalse(lobby.getPlayerList().stream().anyMatch(p -> p.getUsername().equals("P1")));
  }

  @Test
  void removePlayerShouldDoNothingIfPlayerNotExists() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.removePlayer("Ghost");

    assertEquals(1, lobby.getPlayerList().size());
  }

  @Test
  void addPlayerShouldNotAddDuplicatePlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    boolean first = lobby.addPlayer("Max");
    boolean second = lobby.addPlayer("Max");

    assertTrue(first);
    assertFalse(second);

    long count = lobby.getPlayerList().stream().filter(p -> p.getUsername().equals("Max")).count();

    assertEquals(1, count);
  }
}
