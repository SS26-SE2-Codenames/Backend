package com.codenames.codenames.backend.recovery;

import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import org.springframework.stereotype.Service;

/**
 * Persists the current runtime state of the backend.
 *
 * <p>Collects lobby and game snapshots and stores them through the configured {@link
 * JsonStateStore} to enable recovery after application restart.
 */
@Service
public class SystemStatePersistenceService {

  private final JsonStateStore stateStore;
  private final LobbyService lobbyService;
  private final GameService gameService;

  /**
   * Creates a persistence service responsible for storing backend state snapshots.
   *
   * @param stateStore JSON storage implementation
   * @param lobbyService service providing lobby snapshots
   * @param gameService service providing game snapshots
   */
  public SystemStatePersistenceService(
      JsonStateStore stateStore, LobbyService lobbyService, GameService gameService) {

    this.stateStore = stateStore;
    this.lobbyService = lobbyService;
    this.gameService = gameService;
  }

  /**
   * Persists the current backend state.
   *
   * <p>Creates a {@link SystemSnapshot} containing all active lobbies and games and stores it using
   * the configured {@link JsonStateStore}.
   */
  public void persistCurrentState() {

    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION,
            lobbyService.getLobbySnapshots(),
            gameService.getGameSnapshots());

    stateStore.save(snapshot);
  }
}
