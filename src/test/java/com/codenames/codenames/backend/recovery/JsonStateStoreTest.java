package com.codenames.codenames.backend.recovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.recovery.snapshot.ClueSnapshot;
import com.codenames.codenames.backend.recovery.snapshot.GameSnapshot;
import com.codenames.codenames.backend.recovery.snapshot.LobbySnapshot;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonStateStoreTest {

  @TempDir Path tempDir;

  @Test
  void loadReturnsEmptyWhenStateFileDoesNotExist() {
    Path stateFile = tempDir.resolve("state.json");
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());

    Optional<SystemSnapshot> loadedSnapshot = loadSnapshot(stateStore);

    assertTrue(loadedSnapshot.isEmpty());
  }

  @Test
  void loadReturnsEmptyWhenStateFileIsEmpty() throws IOException {
    Path stateFile = tempDir.resolve("state.json");
    Files.createFile(stateFile);
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());

    Optional<SystemSnapshot> loadedSnapshot = loadSnapshot(stateStore);

    assertTrue(loadedSnapshot.isEmpty());
  }

  @Test
  void saveAndLoadRoundTrip() {
    Path stateFile = tempDir.resolve("state.json");
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());

    LobbySnapshot lobbySnapshot =
        new LobbySnapshot(
            "ABCDE",
            List.of(
                new PlayerDto("Host", Team.RED, Role.SPYMASTER, true),
                new PlayerDto("Player", Team.BLUE, Role.OPERATIVE, false)));

    GameSnapshot gameSnapshot =
        new GameSnapshot(
            Team.RED, Role.OPERATIVE, null, 1, 0, 2, new ClueSnapshot("ANIMAL", 2), List.of());

    SystemSnapshot expectedSnapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION,
            Map.of("ABCDE", lobbySnapshot),
            Map.of("ABCDE", gameSnapshot));

    stateStore.save(expectedSnapshot);
    Optional<SystemSnapshot> loadedSnapshot = loadSnapshot(stateStore);

    assertTrue(loadedSnapshot.isPresent());
    assertTrue(Files.exists(stateFile));

    SystemSnapshot actualSnapshot = loadedSnapshot.get();
    assertEquals(SystemSnapshot.CURRENT_SCHEMA_VERSION, actualSnapshot.schemaVersion());
    assertEquals(1, actualSnapshot.lobbies().size());
    assertEquals(1, actualSnapshot.games().size());

    LobbySnapshot actualLobbySnapshot = actualSnapshot.lobbies().get("ABCDE");
    assertEquals("ABCDE", actualLobbySnapshot.lobbyCode());
    assertEquals(2, actualLobbySnapshot.players().size());
    assertEquals("Host", actualLobbySnapshot.players().get(0).username());
    assertEquals(Team.RED, actualLobbySnapshot.players().get(0).team());
    assertEquals(Role.SPYMASTER, actualLobbySnapshot.players().get(0).role());
    assertTrue(actualLobbySnapshot.players().get(0).isHost());

    GameSnapshot actualGameSnapshot = actualSnapshot.games().get("ABCDE");
    assertEquals(Team.RED, actualGameSnapshot.currentTurn());
    assertEquals(Role.OPERATIVE, actualGameSnapshot.currentPhase());
    assertEquals(2, actualGameSnapshot.remainingGuesses());
    assertEquals("ANIMAL", actualGameSnapshot.currentClue().word());
    assertEquals(2, actualGameSnapshot.currentClue().guessAmount());
    assertTrue(actualGameSnapshot.cards().isEmpty());
  }

  @Test
  void saveOverwritesPreviousSnapshot() {
    Path stateFile = tempDir.resolve("state.json");
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());

    SystemSnapshot firstSnapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of(), Map.of());
    SystemSnapshot secondSnapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION,
            Map.of("ABCDE", new LobbySnapshot("ABCDE", List.of())),
            Map.of());

    stateStore.save(firstSnapshot);
    stateStore.save(secondSnapshot);

    Optional<SystemSnapshot> loadedSnapshot = loadSnapshot(stateStore);

    assertTrue(loadedSnapshot.isPresent());
    assertTrue(loadedSnapshot.get().lobbies().containsKey("ABCDE"));
    assertEquals(1, loadedSnapshot.get().lobbies().size());
  }

  @Test
  void saveThrowsIllegalStateWhenStateParentCannotBeCreated() throws IOException {
    Path fileAsParent = tempDir.resolve("not-a-directory");
    Files.writeString(fileAsParent, "occupied");
    Path stateFile = fileAsParent.resolve("state.json");
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());
    SystemSnapshot snapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of(), Map.of());

    assertThrows(IllegalStateException.class, () -> stateStore.save(snapshot));
  }

  @Test
  void loadThrowsIllegalStateWhenJsonIsInvalid() throws IOException {
    Path stateFile = tempDir.resolve("state.json");
    Files.writeString(stateFile, "{invalid-json");
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), stateFile.toString());

    assertThrows(IllegalStateException.class, stateStore::load);
  }

  private Optional<SystemSnapshot> loadSnapshot(JsonStateStore stateStore) {
    return stateStore.load();
  }
}
