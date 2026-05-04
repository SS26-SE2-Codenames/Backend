package com.codenames.codenames.backend.lobby.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import com.codenames.codenames.backend.websocket.Player;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LobbyService}.
 *
 * <p>Validates lobby creation, joining, leaving, and player management behavior.
 */

class LobbyServiceTest {

  private LobbyService lobbyService;
  private LobbyCodeGenerator generator;

  @BeforeEach
  void setup() {
    generator = mock(LobbyCodeGenerator.class);
    lobbyService = new LobbyService(generator);
  }

  @Test
  void createLobbyReturnLobbyCode() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");
    boolean result = lobbyService.joinLobby("TestUser", "ABCDE");

    assertTrue(result);

    List<Player> players = lobbyService.getPlayers("ABCDE");
    assertTrue(players.stream().anyMatch(p -> p.getUsername().equals("TestUser")));
  }

  @Test
  void createLobbyLobbyCodeIsNull() {
    when(generator.generateLobbyCode()).thenReturn(null);
    String result = lobbyService.createLobby("Host");

    assertNull(result);
  }

  @Test
  void createLobbyLobbyCodeIsBlank() {
    when(generator.generateLobbyCode()).thenReturn("");
    String result = lobbyService.createLobby("Host");

    assertNull(result);
  }

  @Test
  void joinLobbyReturnFalseLobbyNotExists() {
    boolean result = lobbyService.joinLobby("TestUser", "ABCDE");
    assertFalse(result);
  }

  @Test
  void leaveLobbyReturnTrueLobbyExists() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");

    boolean result = lobbyService.leaveLobby("Host", "ABCDE");

    assertTrue(result);

    List<Player> players = lobbyService.getPlayers("ABCDE");

    assertFalse(players.stream().anyMatch(p -> p.getUsername().equals("Host")));
  }

  @Test
  void leaveLobbyReturnFalseLobbyNotExists() {
    boolean result = lobbyService.leaveLobby("Host", "ABCDE");
    assertFalse(result);
  }

  @Test
  void createLobbyShouldGenerateNewCodeIfDuplicateExists() {
    when(generator.generateLobbyCode())
        .thenReturn("ABCDE")
        .thenReturn("ABCDE")
        .thenReturn("FGHIJ");

    lobbyService.createLobby("Host1");
    String code2 = lobbyService.createLobby("Host2");

    assertEquals("FGHIJ", code2);
  }

  @Test
  void selectPositionShouldReturnTrueWhenPlayerChoosesTeamAndRole() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");

    boolean result = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);

    assertTrue(result);
  }

  @Test
  void selectPositionShouldReturnFalseWhenLobbyDoesNotExist() {
    boolean result = lobbyService.selectPosition("Host", "XXXXX", Team.RED, Role.SPYMASTER);

    assertFalse(result);
  }

  @Test
  void selectPositionShouldReturnFalseWhenPlayerIsNotInLobby() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");

    boolean result = lobbyService.selectPosition("Ghost", "ABCDE", Team.RED, Role.SPYMASTER);

    assertFalse(result);
  }

  @Test
  void selectPositionShouldReturnFalseWhenSecondSpymasterChoosesSameTeam() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("P1", "ABCDE");

    boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);
    boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.RED, Role.SPYMASTER);

    assertTrue(firstResult);
    assertFalse(secondResult);
  }

  @Test
  void selectPositionShouldReturnTrueWhenSpymastersChooseDifferentTeams() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("P1", "ABCDE");

    boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);
    boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.BLUE, Role.SPYMASTER);

    assertTrue(firstResult);
    assertTrue(secondResult);
  }

  @Test
  void selectPositionShouldReturnTrueWhenMultipleOperativesChooseSameTeam() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("P1", "ABCDE");

    boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.OPERATIVE);
    boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.RED, Role.OPERATIVE);

    assertTrue(firstResult);
    assertTrue(secondResult);
  }

  @Test
  void getPlayersShouldReturnEmptyListWhenLobbyDoesNotExist() {
    List<Player> players = lobbyService.getPlayers("UNKNOWN");

    assertNotNull(players);
    assertTrue(players.isEmpty());
  }

  @Test
  void joinLobbyShouldReturnFalseWhenPlayerAlreadyExists() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");

    lobbyService.createLobby("Host");

    boolean first = lobbyService.joinLobby("Max", "ABCDE");
    boolean second = lobbyService.joinLobby("Max", "ABCDE");

    assertTrue(first);
    assertFalse(second);

    List<Player> players = lobbyService.getPlayers("ABCDE");

    long count = players.stream()
        .filter(p -> p.getUsername().equals("Max"))
        .count();

    assertEquals(1, count);
  }

  @Test
  void selectPositionShouldReturnFalseIfTeamIsNull() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    String lobbyCode = lobbyService.createLobby("Host");

    boolean result = lobbyService.selectPosition("Host", lobbyCode, null, Role.OPERATIVE);

    assertFalse(result);
  }

  @Test
  void selectPositionShouldReturnFalseIfRoleIsNull() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
    String lobbyCode = lobbyService.createLobby("Host");

    boolean result = lobbyService.selectPosition("Host", lobbyCode, Team.RED, null);

    assertFalse(result);
  }
}
