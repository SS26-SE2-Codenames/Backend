package com.codenames.codenames.backend.recovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import com.codenames.codenames.backend.game.dto.GameStateDataTransferObject;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SystemStatePersistenceServiceTest {

  @Test
  void persistCurrentStateSavesCurrentSystemSnapshot() {
    JsonStateStore stateStore = mock(JsonStateStore.class);
    LobbyService lobbyService = mock(LobbyService.class);
    GameService gameService = mock(GameService.class);

    Map<String, List<PlayerDto>> lobbySnapshots = Map.of("ABCDE", List.of());

    Map<String, GameStateDataTransferObject> gameSnapshots = Map.of();

    when(lobbyService.getLobbySnapshots()).thenReturn(lobbySnapshots);
    when(gameService.getGameSnapshots()).thenReturn(gameSnapshots);

    SystemStatePersistenceService persistenceService =
        new SystemStatePersistenceService(stateStore, lobbyService, gameService);

    persistenceService.persistCurrentState();

    ArgumentCaptor<SystemSnapshot> snapshotCaptor = ArgumentCaptor.forClass(SystemSnapshot.class);

    verify(stateStore, times(1)).save(snapshotCaptor.capture());

    SystemSnapshot snapshot = snapshotCaptor.getValue();

    assertEquals(SystemSnapshot.CURRENT_SCHEMA_VERSION, snapshot.schemaVersion());
    assertEquals(lobbySnapshots, snapshot.lobbies());
    assertEquals(gameSnapshots, snapshot.games());
  }
}
