package com.codenames.codenames_backend.lobby.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames_backend.websocket.Player;
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
        .thenReturn("ABCDE") // erster Versuch
        .thenReturn("ABCDE") // zweiter Versuch
        .thenReturn("FGHIJ"); // dritter Versuch - anderes Ergebnis

    lobbyService.createLobby("Host1");
    String code2 = lobbyService.createLobby("Host2");

    assertEquals("FGHIJ", code2);
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

    long count = players.stream().filter(p -> p.getUsername().equals("Max")).count();

    assertEquals(1, count);
  }
}
