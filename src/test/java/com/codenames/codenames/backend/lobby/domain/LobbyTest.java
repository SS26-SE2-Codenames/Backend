package com.codenames.codenames.backend.lobby.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LobbyTest {

  @Test
    void constructorShouldInitializeLobbyCorrectly() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    assertEquals("ABCDE", lobby.getLobbyCode());
    assertEquals(1, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.username().equals("Host")));
    assertNotNull(lobby.getPlayerList().get(0).uuid());
  }

  @Test
    void addPlayerShouldAddPlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1", false);

    assertEquals(2, lobby.getPlayerList().size());
    assertTrue(lobby.getPlayerList().stream().anyMatch(p -> p.username().equals("P1")));
  }

  @Test
    void addPlayerShouldNotExceedMaxPlayers() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.addPlayer("P1", false);
    lobby.addPlayer("P2", false);
    lobby.addPlayer("P3", false);
    Player rejectedPlayer = lobby.addPlayer("P4", false);

    assertEquals(4, lobby.getPlayerList().size());
    assertNull(rejectedPlayer);
  }

  @Test
    void removePlayerShouldRemovePlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    Player p1 = lobby.addPlayer("P1", false);
    String p1Uuid = p1.uuid();

    lobby.removePlayer(p1Uuid);

    assertFalse(lobby.getPlayerList().stream().anyMatch(p -> p.uuid().equals(p1Uuid)));
  }

  @Test
    void removePlayerShouldDoNothingIfPlayerNotExists() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    lobby.removePlayer("invalid-uuid-1234");

    assertEquals(1, lobby.getPlayerList().size());
  }

  @Test
    void addPlayerShouldAllowDuplicateUsernamesWithDifferentUuids() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    Player first = lobby.addPlayer("Max", false);
    Player second = lobby.addPlayer("Max", false);

    assertNotNull(first);
    assertNotNull(second);
    assertNotEquals(first.uuid(), second.uuid());
    assertEquals(3, lobby.getPlayerList().size());
  }

  @Test
    void hasPlayerShouldReturnTrueIfPlayerExists() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    String hostUuid = lobby.getPlayerList().get(0).uuid();

    assertTrue(lobby.hasPlayer(hostUuid));
  }

  @Test
    void hasPlayerShouldReturnFalseIfPlayerDoesNotExist() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    assertFalse(lobby.hasPlayer("invalid-uuid"));
  }

  @Test
    void setPlayerTeamShouldStoreSelectedTeam() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    String hostUuid = lobby.getPlayerList().get(0).uuid();

    lobby.setPlayerTeam(hostUuid, Team.RED);

    assertEquals(Team.RED, lobby.getPlayerTeam(hostUuid));
  }

  @Test
    void setPlayerRoleShouldStoreSelectedRole() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    String hostUuid = lobby.getPlayerList().get(0).uuid();

    lobby.setPlayerRole(hostUuid, Role.SPYMASTER);

    assertEquals(Role.SPYMASTER, lobby.getPlayerRole(hostUuid));
  }

  @Test
    void removePlayerShouldAlsoRemoveStoredTeamAndRole() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    String hostUuid = lobby.getPlayerList().get(0).uuid();

    lobby.setPlayerTeam(hostUuid, Team.BLUE);
    lobby.setPlayerRole(hostUuid, Role.OPERATIVE);

    lobby.removePlayer(hostUuid);

    assertNull(lobby.getPlayerTeam(hostUuid));
    assertNull(lobby.getPlayerRole(hostUuid));
  }

  @Test
    void startingTeamShouldBeEitherRedOrBlue() {
    Lobby lobby = new Lobby("ABCDE", "Host");

    Team startingTeam = lobby.decideStartingTeam();

    assertTrue(startingTeam == Team.RED || startingTeam == Team.BLUE);
  }

  @Test
    void addPlayerShouldAssignUuidToNewPlayer() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    Player newPlayer = lobby.addPlayer("P1", false);

    assertNotNull(newPlayer.uuid());
  }

  @Test
    void addPlayerShouldAllowReconnectWithCorrectUuid() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    Player first = lobby.addPlayer("P1", false);
    String validUuid = first.uuid();

    Player reconnected = lobby.addPlayer("P1", false, validUuid);

    assertNotNull(reconnected);
    assertEquals(validUuid, reconnected.uuid());
  }

  @Test
    void addPlayerShouldBlockReconnectWithWrongUuid() {
    Lobby lobby = new Lobby("ABCDE", "Host");
    lobby.addPlayer("P1", false);

    Player blocked = lobby.addPlayer("P1", false, "WRONG-UUID");

    assertNull(blocked);
  }

  @Test
    void addRestoredPlayerShouldAddPlayerDirectly() {
    Lobby lobby = new Lobby("ABCDE");
    Player restoredPlayer = new Player("RestoredUser", false, "custom-uuid-999");

    lobby.addRestoredPlayer(restoredPlayer);

    assertEquals(1, lobby.getPlayerList().size());
    assertEquals("custom-uuid-999", lobby.getPlayerList().get(0).uuid());
    assertEquals("RestoredUser", lobby.getPlayerList().get(0).username());
  }
}