package com.codenames.codenames.backend.recovery;

import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import org.springframework.stereotype.Service;

@Service
public class SystemStatePersistenceService {

  private final JsonStateStore stateStore;
  private final LobbyService lobbyService;
  private final GameService gameService;

  public SystemStatePersistenceService(
          JsonStateStore stateStore, LobbyService lobbyService, GameService gameService) {

    this.stateStore = stateStore;
    this.lobbyService = lobbyService;
    this.gameService = gameService;
  }

  public void persistCurrentState() {

    SystemSnapshot snapshot =
        new SystemSnapshot(
            SystemSnapshot.CURRENT_SCHEMA_VERSION,
            lobbyService.getLobbySnapshots(),
            gameService.getGameSnapshots());

    stateStore.save(snapshot);
  }
}
