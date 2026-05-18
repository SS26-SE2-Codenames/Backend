package com.codenames.codenames.backend.recovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codenames.codenames.backend.chat.ChatService;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.lobby.Lobby;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.services.LobbyCodeGenerator;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.playingfield.GameManagerFactory;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.recovery.snapshot.ClueSnapshot;
import com.codenames.codenames.backend.recovery.snapshot.GameSnapshot;
import com.codenames.codenames.backend.recovery.snapshot.LobbySnapshot;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import com.codenames.codenames.backend.serialization.CardDataTransferObject;
import com.codenames.codenames.backend.serialization.DataTransferObjectService;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SystemStateRecoveryServiceTest {

  @TempDir Path tempDir;

  @Test
  void recoverOnStartupDoesNothingWhenNoSnapshotExists() {
    TestContext context = createContext(tempDir.resolve("state.json"));

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
    assertFalse(context.gameService().isGameStarted("ABCDE"));
  }

  @Test
  void recoverOnStartupSkipsSnapshotWhenSchemaVersionDiffers() {
    TestContext context = createContext(tempDir.resolve("state.json"));
    SystemSnapshot snapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION + 1, Map.of(), Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
    assertFalse(context.gameService().isGameStarted("ABCDE"));
  }

  @Test
  void recoverOnStartupRestoresLobbiesAndGamesFromSnapshot() {
    TestContext context = createContext(tempDir.resolve("state.json"));
    LobbySnapshot lobbySnapshot =
        new LobbySnapshot(
            "ABCDE",
            List.of(
                new PlayerDto("Host", Team.RED, Role.SPYMASTER, true),
                new PlayerDto("Player", Team.BLUE, Role.OPERATIVE, false)));
    GameSnapshot gameSnapshot =
        new GameSnapshot(
            Team.RED,
            Role.OPERATIVE,
            null,
            1,
            0,
            2,
            new ClueSnapshot("ANIMAL", 2),
            List.of(
                new CardDataTransferObject("Dog", Color.RED, true),
                new CardDataTransferObject("Cat", Color.BLUE, false)));
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION,
            Map.of("ABCDE", lobbySnapshot),
            Map.of("ABCDE", gameSnapshot));
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    Lobby restoredLobby = context.lobbyService().getLobbyList().get("ABCDE");
    assertEquals(2, restoredLobby.getPlayerList().size());
    assertEquals(Team.RED, restoredLobby.getPlayerTeam("Host"));
    assertEquals(Role.SPYMASTER, restoredLobby.getPlayerRole("Host"));
    assertEquals(Team.BLUE, restoredLobby.getPlayerTeam("Player"));
    assertEquals(Role.OPERATIVE, restoredLobby.getPlayerRole("Player"));

    assertTrue(context.gameService().isGameStarted("ABCDE"));
    GameManager restoredGame = context.gameService().getGameState("ABCDE");
    assertEquals(Team.RED, restoredGame.getCurrentTurn());
    assertEquals(Role.OPERATIVE, restoredGame.getCurrentPhase());
    assertEquals(2, restoredGame.getRemainingGuesses());
    assertEquals("ANIMAL", restoredGame.getCurrentClue().word());
    assertTrue(restoredGame.getCardList().get(0).isGuessed());
    assertFalse(restoredGame.getCardList().get(1).isGuessed());
  }

  @Test
  void recoverOnStartupWithCurrentSchemaAndEmptyMapsDoesNothing() {
    TestContext context = createContext(tempDir.resolve("state-empty.json"));
    SystemSnapshot snapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of(), Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
    assertFalse(context.gameService().isGameStarted("ABCDE"));
  }

  @Test
  void recoverOnStartupHandlesNullLobbyAndGameMaps() {
    TestContext context = createContext(tempDir.resolve("state-null-maps.json"));
    SystemSnapshot snapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION, null, null);
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
    assertFalse(context.gameService().isGameStarted("ABCDE"));
  }

  @Test
  void recoverOnStartupSkipsLobbyWhenSnapshotEntryIsNull() {
    TestContext context = createContext(tempDir.resolve("state-null-lobby-entry.json"));
    Map<String, LobbySnapshot> lobbies = new HashMap<>();
    lobbies.put("ABCDE", null);
    SystemSnapshot snapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION, lobbies, Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
  }

  @Test
  void recoverOnStartupSkipsLobbyWhenPlayersListIsNull() {
    TestContext context = createContext(tempDir.resolve("state-null-players.json"));
    LobbySnapshot lobbySnapshot = new LobbySnapshot("ABCDE", null);
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of("ABCDE", lobbySnapshot), Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
  }

  @Test
  void recoverOnStartupSkipsLobbyWhenPlayersListIsEmpty() {
    TestContext context = createContext(tempDir.resolve("state-empty-players.json"));
    LobbySnapshot lobbySnapshot = new LobbySnapshot("ABCDE", List.of());
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of("ABCDE", lobbySnapshot), Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
  }

  @Test
  void recoverOnStartupSkipsLobbyWhenAllUsernamesAreInvalid() {
    TestContext context = createContext(tempDir.resolve("state-invalid-usernames.json"));
    LobbySnapshot lobbySnapshot =
        new LobbySnapshot(
            "ABCDE",
            List.of(
                new PlayerDto("   ", Team.RED, Role.SPYMASTER, true),
                new PlayerDto(null, Team.BLUE, Role.OPERATIVE, false)));
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of("ABCDE", lobbySnapshot), Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
  }

  @Test
  void recoverOnStartupRestoresLobbyWhenSomeTeamOrRoleValuesAreMissing() {
    TestContext context = createContext(tempDir.resolve("state-missing-team-role.json"));
    LobbySnapshot lobbySnapshot =
        new LobbySnapshot(
            "ABCDE",
            List.of(
                new PlayerDto("Host", Team.RED, Role.SPYMASTER, true),
                new PlayerDto("Player", null, null, false)));
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of("ABCDE", lobbySnapshot), Map.of());
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    Lobby restoredLobby = context.lobbyService().getLobbyList().get("ABCDE");
    assertEquals(Team.RED, restoredLobby.getPlayerTeam("Host"));
    assertEquals(Role.SPYMASTER, restoredLobby.getPlayerRole("Host"));
    assertNull(restoredLobby.getPlayerTeam("Player"));
    assertNull(restoredLobby.getPlayerRole("Player"));
  }

  @Test
  void recoverOnStartupRestoresOnlyGamesWhenLobbiesMapIsNull() {
    TestContext context = createContext(tempDir.resolve("state-lobbies-null-games-present.json"));
    GameSnapshot gameSnapshot =
        new GameSnapshot(
            Team.BLUE,
            Role.SPYMASTER,
            null,
            0,
            0,
            0,
            null,
            List.of(new CardDataTransferObject("Tree", Color.BLUE, false)));
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION, null, Map.of("ABCDE", gameSnapshot));
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().isEmpty());
    assertTrue(context.gameService().isGameStarted("ABCDE"));
  }

  @Test
  void recoverOnStartupRestoresOnlyLobbiesWhenGamesMapIsNull() {
    TestContext context = createContext(tempDir.resolve("state-games-null-lobbies-present.json"));
    LobbySnapshot lobbySnapshot =
        new LobbySnapshot("ABCDE", List.of(new PlayerDto("Host", Team.RED, Role.SPYMASTER, true)));
    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of("ABCDE", lobbySnapshot), null);
    context.stateStore().save(snapshot);

    context.recoveryService().recoverOnStartup();

    assertTrue(context.lobbyService().getLobbyList().containsKey("ABCDE"));
    assertFalse(context.gameService().isGameStarted("ABCDE"));
  }

  private TestContext createContext(Path stateFile) {
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());
    GameManagerFactory gameManagerFactory =
        new GameManagerFactory(
            new CardGenerator("CodenamesWordlist.txt"), new ClueValidationService());
    GameService gameService = new GameService(gameManagerFactory, new DataTransferObjectService());
    LobbyService lobbyService =
        new LobbyService(new LobbyCodeGenerator(), new ChatService(null), gameService);
    SystemStateRecoveryService recoveryService =
        new SystemStateRecoveryService(stateStore, lobbyService, gameService, gameManagerFactory);

    return new TestContext(stateStore, lobbyService, gameService, recoveryService);
  }

  private record TestContext(
      JsonStateStore stateStore,
      LobbyService lobbyService,
      GameService gameService,
      SystemStateRecoveryService recoveryService) {}
}
