package com.codenames.codenames.backend.recovery.snapshot;

import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.game.dto.GameStateDataTransferObject;
import java.util.List;
import java.util.Map;

/**
 * Root snapshot aggregate for persisted backend runtime state.
 *
 * @param schemaVersion persisted schema version
 * @param lobbies lobby player lists keyed by lobby code
 * @param games game states keyed by lobby code
 */
public record SystemSnapshot(
    int schemaVersion,
    Map<String, List<PlayerDto>> lobbies,
    Map<String, GameStateDataTransferObject> games) {

  public static final int CURRENT_SCHEMA_VERSION = 2;
}
