package com.codenames.codenames.backend.lobby.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.username().equals("Host")));
  }

  @Test
  void addPlayerShouldAddPlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1", null);

    assertEquals(2, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.username().equals("P1")));
  }

  @Test
  void addPlayerShouldNotExceedMaxPlayers() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1", null);
    lobby.addPlayer("P2", null);
    lobby.addPlayer("P3", null);
    lobby.addPlayer("P4", null);

    assertEquals(4, lobby.getPlayerList().size());
  }

  @Test
  void removePlayerShouldRemovePlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1", null);
    lobby.removePlayer("P1");

    assertFalse(lobby.getPlayerList().stream().anyMatch(p -> p.username().equals("P1")));
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

    Player first = lobby.addPlayer("Max", null);
    Player second = lobby.addPlayer("Max", null);

    assertNotNull(first);
    assertNull(second);

    long count = lobby.getPlayerList().stream().filter(p -> p.username().equals("Max")).count();

    assertEquals(1, count);
  }

  @Test
  void hasPlayerShouldReturnTrueIfPlayerExists() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    assertTrue(lobby.hasPlayer("Host"));
  }

  @Test
  void hasPlayerShouldReturnFalseIfPlayerDoesNotExist() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    assertFalse(lobby.hasPlayer("Ghost"));
  }

  @Test
  void setPlayerTeamShouldStoreSelectedTeam() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.setPlayerTeam("Host", Team.RED);

    assertEquals(Team.RED, lobby.getPlayerTeam("Host"));
  }

  @Test
  void setPlayerRoleShouldStoreSelectedRole() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.setPlayerRole("Host", Role.SPYMASTER);

    assertEquals(Role.SPYMASTER, lobby.getPlayerRole("Host"));
  }

  @Test
  void removePlayerShouldAlsoRemoveStoredTeamAndRole() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.setPlayerTeam("Host", Team.BLUE);
    lobby.setPlayerRole("Host", Role.OPERATIVE);

    lobby.removePlayer("Host");

    assertNull(lobby.getPlayerTeam("Host"));
    assertNull(lobby.getPlayerRole("Host"));
  }

  @Test
  void startingTeamShouldBeEitherRedOrBlue() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    Team startingTeam = lobby.decideStartingTeam();

    assertTrue(startingTeam == Team.RED || startingTeam == Team.BLUE);
  }
}
