package com.codenames.codenames_backend.lobby;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

  @Test
  void constructor_shouldInitializeLobbyCorrectly() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    assertEquals("ABCDE", lobby.getLobbyCode());
    assertEquals(1, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.getUsername().equals("Host")));
  }

  @Test
  void addPlayer_shouldAddPlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1");

    assertEquals(2, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.getUsername().equals("P1")));
  }

  @Test
  void addPlayer_shouldNotExceedMaxPlayers() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1");
    lobby.addPlayer("P2");
    lobby.addPlayer("P3");
    lobby.addPlayer("P4"); // sollte ignoriert werden

    assertEquals(4, lobby.getPlayerList().size());
  }

  @Test
  void removePlayer_shouldRemovePlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1");
    lobby.removePlayer("P1");

    assertFalse(lobby.getPlayerList().stream().anyMatch(p -> p.getUsername().equals("P1")));
  }

  @Test
  void removePlayer_shouldDoNothingIfPlayerNotExists() {
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
  void hasPlayer_shouldReturnTrueIfPlayerExists() {
      Lobby lobby = new Lobby("ABCDE", "Host");

      assertTrue(lobby.hasPlayer("Host"));
  }

  @Test
  void hasPlayer_shouldReturnFalseIfPlayerDoesNotExist() {
      Lobby lobby = new Lobby("ABCDE", "Host");

      assertFalse(lobby.hasPlayer("Ghost"));
  }

  @Test
  void setPlayerTeam_shouldStoreSelectedTeam() {
      Lobby lobby = new Lobby("ABCDE", "Host");

      lobby.setPlayerTeam("Host", Team.RED);

      assertEquals(Team.RED, lobby.getPlayerTeam("Host"));
  }

  @Test
  void setPlayerRole_shouldStoreSelectedRole() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.setPlayerRole("Host", Role.SPYMASTER);

    assertEquals(Role.SPYMASTER, lobby.getPlayerRole("Host"));
  }

  @Test
  void removePlayer_shouldAlsoRemoveStoredTeamAndRole() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.setPlayerTeam("Host", Team.BLUE);
    lobby.setPlayerRole("Host", Role.OPERATIVE);

    lobby.removePlayer("Host");

    assertNull(lobby.getPlayerTeam("Host"));
    assertNull(lobby.getPlayerRole("Host"));
  }
}
