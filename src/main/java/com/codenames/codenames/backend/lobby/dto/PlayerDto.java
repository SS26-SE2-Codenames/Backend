package com.codenames.codenames.backend.lobby.dto;

import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;

public record PlayerDto(String username, Team team, Role role, boolean isHost) {
}
