package com.codenames.codenames.backend.recovery.application;

import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.game.domain.GameManagerFactory;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.recovery.domain.snapshot.SystemSnapshot;
import com.codenames.codenames.backend.recovery.infrastructure.JsonStateStore;
import com.codenames.codenames.backend.game.dto.GameStateDataTransferObject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Restores persisted lobbies and games into in-memory runtime services on backend startup. */
@Slf4j
@Service
public class SystemStateRecoveryService {

  private final JsonStateStore stateStore;
  private final LobbyService lobbyService;
  private final GameService gameService;
  private final GameManagerFactory gameManagerFactory;

  /**
   * Creates a recovery service.
   *
   * @param stateStore JSON state store used for loading snapshots
   * @param lobbyService lobby runtime service
   * @param gameService game runtime service
   * @param gameManagerFactory factory used to rebuild game managers from snapshots
   */
  public SystemStateRecoveryService(
      JsonStateStore stateStore,
      LobbyService lobbyService,
      GameService gameService,
      GameManagerFactory gameManagerFactory) {
    this.stateStore = stateStore;
    this.lobbyService = lobbyService;
    this.gameService = gameService;
    this.gameManagerFactory = gameManagerFactory;
  }

  /** Loads and restores persisted state at startup when a compatible snapshot exists. */
  @jakarta.annotation.PostConstruct
  public void recoverOnStartup() {
    stateStore
        .load()
        .ifPresent(
            snapshot -> {
              if (snapshot.schemaVersion() != SystemSnapshot.CURRENT_SCHEMA_VERSION) {
                log.warn(
                    "Skipping snapshot restore due to schema mismatch. Found {}, expected {}.",
                    snapshot.schemaVersion(),
                    SystemSnapshot.CURRENT_SCHEMA_VERSION);
                return;
              }
              restoreLobbies(snapshot.lobbies());
              restoreGames(snapshot.games());
            });
  }

  /**
   * Restores all lobby snapshots into {@link LobbyService}.
   *
   * @param lobbySnapshots persisted lobby player lists keyed by lobby code
   */
  private void restoreLobbies(Map<String, List<PlayerDto>> lobbySnapshots) {
    if (lobbySnapshots == null || lobbySnapshots.isEmpty()) {
      return;
    }
    for (Map.Entry<String, List<PlayerDto>> entry : lobbySnapshots.entrySet()) {
      Lobby restoredLobby = buildLobby(entry.getKey(), entry.getValue());
      if (restoredLobby != null) {
        lobbyService.restoreLobby(entry.getKey(), restoredLobby);
      }
    }
  }

  /**
   * Restores all game snapshots into {@link GameService}.
   *
   * @param gameSnapshots persisted game states keyed by lobby code
   */
  private void restoreGames(Map<String, GameStateDataTransferObject> gameSnapshots) {
    if (gameSnapshots == null || gameSnapshots.isEmpty()) {
      return;
    }
    for (Map.Entry<String, GameStateDataTransferObject> entry : gameSnapshots.entrySet()) {
      GameManager restoredGame = gameManagerFactory.createFromSnapshot(entry.getValue());
      gameService.restoreGameManager(entry.getKey(), restoredGame);
    }
  }

  /**
   * Builds a runtime lobby from a persisted lobby snapshot.
   *
   * @param lobbyCode target lobby code
   * @param players persisted lobby players
   * @return rebuilt lobby, or {@code null} when player data is invalid
   */
  private Lobby buildLobby(String lobbyCode, List<PlayerDto> players) {
    if (players == null || players.isEmpty()) {
      log.warn("Skipping restore for lobby {} due to missing player data.", lobbyCode);
      return null;
    }

    List<PlayerDto> validPlayers =
        players.stream()
            .filter(player -> player.username() != null && !player.username().isBlank())
            .sorted(Comparator.comparing(PlayerDto::isHost).reversed())
            .toList();

    if (validPlayers.isEmpty()) {
      return null;
    }

    PlayerDto host = validPlayers.get(0);
    Lobby lobby = new Lobby(lobbyCode, host.username());

    for (PlayerDto player : validPlayers) {
      if (!player.username().equals(host.username())) {
        lobby.addPlayer(player.username(), player.isHost());
      }
      if (player.team() != null) {
        lobby.setPlayerTeam(player.username(), player.team());
      }
      if (player.role() != null) {
        lobby.setPlayerRole(player.username(), player.role());
      }
    }

    return lobby;
  }
}
