package com.codenames.codenames.backend.recovery;

import com.codenames.codenames.backend.lobby.Lobby;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.playingfield.GameManagerFactory;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.recovery.snapshot.GameSnapshot;
import com.codenames.codenames.backend.recovery.snapshot.LobbySnapshot;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemStateRecoveryService {

  private final JsonStateStore stateStore;
  private final LobbyService lobbyService;
  private final GameService gameService;
  private final GameManagerFactory gameManagerFactory;

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

  private void restoreLobbies(Map<String, LobbySnapshot> lobbySnapshots) {
    if (lobbySnapshots == null || lobbySnapshots.isEmpty()) {
      return;
    }
    for (Map.Entry<String, LobbySnapshot> entry : lobbySnapshots.entrySet()) {
      Lobby restoredLobby = buildLobby(entry.getKey(), entry.getValue());
      if (restoredLobby != null) {
        lobbyService.restoreLobby(entry.getKey(), restoredLobby);
      }
    }
  }

  private void restoreGames(Map<String, GameSnapshot> gameSnapshots) {
    if (gameSnapshots == null || gameSnapshots.isEmpty()) {
      return;
    }
    for (Map.Entry<String, GameSnapshot> entry : gameSnapshots.entrySet()) {
      GameManager restoredGame = gameManagerFactory.createFromSnapshot(entry.getValue());
      gameService.restoreGameManager(entry.getKey(), restoredGame);
    }
  }

  private Lobby buildLobby(String lobbyCode, LobbySnapshot snapshot) {
    if (snapshot == null || snapshot.players() == null || snapshot.players().isEmpty()) {
      log.warn("Skipping restore for lobby {} due to missing player data.", lobbyCode);
      return null;
    }

    List<PlayerDto> players =
        snapshot.players().stream()
            .filter(player -> player.username() != null && !player.username().isBlank())
            .sorted(Comparator.comparing(PlayerDto::isHost).reversed())
            .toList();

    if (players.isEmpty()) {
      return null;
    }

    PlayerDto host = players.get(0);
    Lobby lobby = new Lobby(lobbyCode, host.username());

    for (PlayerDto player : players) {
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
