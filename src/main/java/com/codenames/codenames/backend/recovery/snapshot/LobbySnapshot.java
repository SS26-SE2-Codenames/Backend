package com.codenames.codenames.backend.recovery.snapshot;

import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import java.util.List;

public record LobbySnapshot(String lobbyCode, List<PlayerDto> players) {}
