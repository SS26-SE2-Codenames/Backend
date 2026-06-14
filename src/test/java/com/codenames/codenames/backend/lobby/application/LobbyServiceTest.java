package com.codenames.codenames.backend.lobby.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.chat.application.ChatService;
import com.codenames.codenames.backend.database.repository.LobbyRepository;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import com.codenames.codenames.backend.lobby.domain.Player;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link LobbyService}.
 *
 * <p>Validates lobby creation, joining, leaving, and player management behavior.
 */
class LobbyServiceTest {

  private LobbyService lobbyService;
  private LobbyCodeGenerator generator;
  private GameService gameService;
  private LobbyRepository lobbyRepository;

  @BeforeEach
  void setup() {
    generator = mock(LobbyCodeGenerator.class);
    gameService = mock(GameService.class);
    ChatService chatService = mock(ChatService.class);
    lobbyRepository = mock(LobbyRepository.class);

    lobbyService = new LobbyService(generator, chatService, gameService, lobbyRepository);
    when(generator.generateLobbyCode()).thenReturn("ABCDE");
  }

  @Test
  void createLobbyReturnLobbyCode() {
    lobbyService.createLobby("Host");
    Player result = lobbyService.joinLobby("TestUser", "ABCDE", null);

    assertNotNull(result);

    List<Player> players = lobbyService.getPlayers("ABCDE");
    assertTrue(players.stream().anyMatch(p -> p.username().equals("TestUser")));
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
    Player result = lobbyService.joinLobby("TestUser", "ABCDE", null);
    assertNull(result);
  }

  @Test
  void leaveLobbyReturnTrueLobbyExists() {
    lobbyService.createLobby("Host");

    boolean result = lobbyService.leaveLobby("Host", "ABCDE");

    assertTrue(result);

    List<Player> players = lobbyService.getPlayers("ABCDE");

    assertFalse(players.stream().anyMatch(p -> p.username().equals("Host")));
  }

  @Test
  void leaveLobbyReturnFalseLobbyNotExists() {
    boolean result = lobbyService.leaveLobby("Host", "ABCDE");
    assertFalse(result);
  }

  @Test
  void createLobbyShouldGenerateNewCodeIfDuplicateExists() {
    when(generator.generateLobbyCode()).thenReturn("ABCDE").thenReturn("ABCDE").thenReturn("FGHIJ");

    lobbyService.createLobby("Host1");
    String code2 = lobbyService.createLobby("Host2");

    assertEquals("FGHIJ", code2);
  }

  @Test
  void selectPositionShouldReturnTrueWhenPlayerChoosesTeamAndRole() {
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
    lobbyService.createLobby("Host");

    boolean result = lobbyService.selectPosition("Ghost", "ABCDE", Team.RED, Role.SPYMASTER);

    assertFalse(result);
  }

  @Test
  void selectPositionShouldReturnFalseWhenSecondSpymasterChoosesSameTeam() {
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("P1", "ABCDE", null);

    boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);
    boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.RED, Role.SPYMASTER);

    assertTrue(firstResult);
    assertFalse(secondResult);
  }

  @Test
  void selectPositionShouldReturnTrueWhenSpymastersChooseDifferentTeams() {
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("P1", "ABCDE", null);

    boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);
    boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.BLUE, Role.SPYMASTER);

    assertTrue(firstResult);
    assertTrue(secondResult);
  }

  @Test
  void selectPositionShouldReturnTrueWhenMultipleOperativesChooseSameTeam() {
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("P1", "ABCDE", null);

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
    lobbyService.createLobby("Host");

    Player first = lobbyService.joinLobby("Max", "ABCDE", null);
    Player second = lobbyService.joinLobby("Max", "ABCDE", null);

    assertNotNull(first);
    assertNull(second);

    List<Player> players = lobbyService.getPlayers("ABCDE");

    long count = players.stream().filter(p -> p.username().equals("Max")).count();

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

  @Test
  void testGetPlayerTeam() {
    lobbyService.createLobby("Host");
    lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);

    assertEquals(Team.RED, lobbyService.getPlayerTeam("Host", "ABCDE"));
  }

  @Test
  void getPlayerTeam_wrongCode() {
    assertNull(lobbyService.getPlayerTeam("Host", "invalidCode"));
  }

  @Test
  void getPlayerTeam_nonExistentPlayer() {
    String lobbyCode = lobbyService.createLobby("Host");

    assertNull(lobbyService.getPlayerTeam("nonExistentPlayer", lobbyCode));
  }

  @Test
  void testGetPlayerRole() {
    lobbyService.createLobby("Host");
    lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.OPERATIVE);

    assertEquals(Role.OPERATIVE, lobbyService.getPlayerRole("Host", "ABCDE"));
  }

  @Test
  void getPlayerRole_wrongCode() {
    assertNull(lobbyService.getPlayerRole("Host", "test"));
  }

  @Test
  void getPlayerRole_nonExistentPlayer() {
    String lobbyCode = lobbyService.createLobby("Host");

    assertNull(lobbyService.getPlayerRole("nonExistentPlayer", lobbyCode));
  }

  @Test
  void testLobbyIsRemovedWhenItIsEmpty() {
    lobbyService.createLobby("Host");
    lobbyService.leaveLobby("Host", "ABCDE");
    lobbyService.checkLobbyStillHasPlayers("ABCDE");
    assertFalse(lobbyService.getLobbyList().containsKey("ABCDE"));
    verify(gameService, times(1)).removeGame("ABCDE");
    verify(lobbyRepository, times(1)).deleteById("ABCDE");
  }

  @Test
  void testLobbyIsNotRemovedWhenItHasPlayers() {
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("Player1", "ABCDE", null);
    lobbyService.checkLobbyStillHasPlayers("ABCDE");
    assertTrue(lobbyService.getLobbyList().containsKey("ABCDE"));
  }

  @Test
  void testGetPlayersDto() {
    lobbyService.createLobby("Host");

    List<PlayerDto> players = lobbyService.getPlayersDto("ABCDE");
    PlayerDto player = players.get(0);
    assertEquals("Host", player.username());
    assertTrue(player.isHost());
  }

  @Test
  void testGetPlayersDto_lobbyNotExists() {
    List<PlayerDto> players = lobbyService.getPlayersDto("UNKNOWN");
    assertNotNull(players);
    assertTrue(players.isEmpty());
  }

  @Test
  void getPlayersDtoShouldReturnPlayerDtosWhenLobbyExists() {
    lobbyService.createLobby("Host");

    List<PlayerDto> result = lobbyService.getPlayersDto("ABCDE");

    assertNotNull(result);
    assertEquals(1, result.size());

    PlayerDto player = result.get(0);

    assertEquals("Host", player.username());
    assertNull(player.team());
    assertNull(player.role());
    assertTrue(player.isHost());
  }

  @Test
  void getPlayersDtoShouldReturnEmptyList_whenLobbyDoesNotExist() {
    List<PlayerDto> result = lobbyService.getPlayersDto("ABCDE");

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testAddGameManagerForLobby() {
    lobbyService.createLobby("User");
    lobbyService.startGame("ABCDE", "User");
    verify(gameService, times(1)).createGameManager(eq("ABCDE"), any(Team.class));
  }

  @Test
  void testGetIsStarted() {
    when(gameService.isGameStarted("ABCDE")).thenReturn(true);

    boolean result = lobbyService.getIsStarted("ABCDE");
    assertTrue(result);
  }

  @Test
  void testGetIsStartedGameServiceReturnsFalse() {
    when(gameService.isGameStarted("ABCDE")).thenReturn(false);

    boolean result = lobbyService.getIsStarted("ABCDE");
    assertFalse(result);
  }

  @Test
  void testGetHostWorks() {
    lobbyService.createLobby("Alice");
    lobbyService.joinLobby("Bob", "ABCDE", null);
    lobbyService.joinLobby("Caesar", "ABCDE", null);

    String expected = "Alice";
    String result = lobbyService.getHost("ABCDE");

    assertEquals(expected, result);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"ABCDE"})
  void testGetHostReturnsEmptyString(String lobbyCode) {
    String result = lobbyService.getHost(lobbyCode);

    assertEquals("", result);
  }

  @Test
  void testRestoreLobbyAddsLobbyToLobbyList() {
    Lobby restoredLobby = new Lobby("ABCDE", "Host");

    lobbyService.restoreLobby("ABCDE", restoredLobby);

    assertTrue(lobbyService.getLobbyList().containsKey("ABCDE"));
    assertEquals(restoredLobby, lobbyService.getLobbyList().get("ABCDE"));
  }

  @Test
  void getLobbySnapshotsShouldReturnAllLobbyPlayerDtos() {
    lobbyService.createLobby("Host");
    lobbyService.joinLobby("Player", "ABCDE", null);
    lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);

    Map<String, List<PlayerDto>> snapshots = lobbyService.getLobbySnapshots();

    assertTrue(snapshots.containsKey("ABCDE"));
    assertEquals(2, snapshots.get("ABCDE").size());

    PlayerDto host =
            snapshots.get("ABCDE").stream()
                    .filter(player -> player.username().equals("Host"))
                    .findFirst()
                    .orElseThrow();

    assertEquals(Team.RED, host.team());
    assertEquals(Role.SPYMASTER, host.role());
    assertTrue(host.isHost());
  }

  @Test
  void joinLobbyShouldBlockNewPlayerIfGameStarted() {
    when(gameService.isGameStarted("ABCDE")).thenReturn(true);

    Player result = lobbyService.joinLobby("NewPlayer", "ABCDE", null);

    assertNull(result);
  }

  @Test
  void joinLobbyShouldAllowReconnectIfGameStarted() {
    lobbyService.getLobbyList().clear();

    String lobbyCode = lobbyService.createLobby("Host");

    when(gameService.isGameStarted(lobbyCode)).thenReturn(false);

    Player p1 = lobbyService.joinLobby("P1", lobbyCode, null);
    assertNotNull(p1);

    when(gameService.isGameStarted(lobbyCode)).thenReturn(true);

    Player result = lobbyService.joinLobby("P1", lobbyCode, p1.uuid());

    assertNotNull(result);
    assertEquals(p1.uuid(), result.uuid());
  }
}