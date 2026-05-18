package com.codenames.codenames.backend.recovery.snapshot;

import java.util.Map;

public record SystemSnapshot(
    int schemaVersion, Map<String, LobbySnapshot> lobbies, Map<String, GameSnapshot> games) {

  public static final int CURRENT_SCHEMA_VERSION = 1;
}
