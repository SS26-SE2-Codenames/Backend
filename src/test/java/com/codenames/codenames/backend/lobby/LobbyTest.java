package com.codenames.codenames.backend.lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
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
    lobby.addPlayer("P4");

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
