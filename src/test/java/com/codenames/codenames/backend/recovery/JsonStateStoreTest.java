package com.codenames.codenames.backend.recovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codenames.codenames.backend.game.dto.ClueDto;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import com.codenames.codenames.backend.serialization.GameStateDataTransferObject;
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

    List<PlayerDto> lobbyPlayers =
        List.of(
            new PlayerDto("Host", Team.RED, Role.SPYMASTER, true),
            new PlayerDto("Player", Team.BLUE, Role.OPERATIVE, false));

    GameStateDataTransferObject gameSnapshot =
        new GameStateDataTransferObject(
            null, Team.RED, Role.OPERATIVE, new ClueDto("ANIMAL", 2), List.of());

    SystemSnapshot expectedSnapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION,
            Map.of("ABCDE", lobbyPlayers),
            Map.of("ABCDE", gameSnapshot));

    stateStore.save(expectedSnapshot);
    Optional<SystemSnapshot> loadedSnapshot = loadSnapshot(stateStore);

    assertTrue(loadedSnapshot.isPresent());
    assertTrue(Files.exists(stateFile));

    SystemSnapshot actualSnapshot = loadedSnapshot.get();
    assertEquals(SystemSnapshot.CURRENT_SCHEMA_VERSION, actualSnapshot.schemaVersion());
    assertEquals(1, actualSnapshot.lobbies().size());
    assertEquals(1, actualSnapshot.games().size());

    List<PlayerDto> actualPlayers = actualSnapshot.lobbies().get("ABCDE");
    assertEquals(2, actualPlayers.size());
    assertEquals("Host", actualPlayers.get(0).username());
    assertEquals(Team.RED, actualPlayers.get(0).team());
    assertEquals(Role.SPYMASTER, actualPlayers.get(0).role());
    assertTrue(actualPlayers.get(0).isHost());

    GameStateDataTransferObject actualGameSnapshot = actualSnapshot.games().get("ABCDE");
    assertEquals(Team.RED, actualGameSnapshot.currentTurn());
    assertEquals(Role.OPERATIVE, actualGameSnapshot.currentPhase());
    assertEquals("ANIMAL", actualGameSnapshot.currentClue().word());
    assertEquals(2, actualGameSnapshot.currentClue().guessAmount());
    assertTrue(actualGameSnapshot.cardList().isEmpty());
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
            Map.of("ABCDE", List.of()),
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

  @Test
  void gettersExposeConfiguredDependencies() {
    ObjectMapper mapper = new ObjectMapper();
    Path stateFile = tempDir.resolve("state.json");
    JsonStateStore stateStore = new JsonStateStore(mapper, stateFile.toString());

    assertSame(mapper, stateStore.getObjectMapper());
    assertEquals(stateFile, stateStore.getStateFilePath());
    assertNotNull(stateStore.getIoLock());
  }

  @Test
  void saveWorksWhenStateFileHasNoParentDirectory() throws IOException {
    String fileName = "json-state-store-" + System.nanoTime() + ".json";
    Path stateFile = Path.of(fileName);
    JsonStateStore stateStore = new JsonStateStore(new ObjectMapper(), fileName);
    SystemSnapshot snapshot =
        new SystemSnapshot(SystemSnapshot.CURRENT_SCHEMA_VERSION, Map.of(), Map.of());

    try {
      stateStore.save(snapshot);

      assertTrue(Files.exists(stateFile));
      assertTrue(stateStore.load().isPresent());
    } finally {
      Files.deleteIfExists(stateFile);
      Files.deleteIfExists(Path.of(fileName + ".tmp"));
    }
  }

  private Optional<SystemSnapshot> loadSnapshot(JsonStateStore stateStore) {
    return stateStore.load();
  }
}
