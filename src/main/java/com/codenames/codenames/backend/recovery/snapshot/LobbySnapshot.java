package com.codenames.codenames.backend.recovery.snapshot;

import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import java.util.List;

/**
 * Persisted lobby payload used for restart recovery snapshots.
 *
 * @param lobbyCode lobby identifier
 * @param players players including team/role/host metadata
 */
public record LobbySnapshot(String lobbyCode, List<PlayerDto> players) {}
