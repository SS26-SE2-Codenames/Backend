package com.codenames.codenames.backend.lobby.dto;

import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;

/**
 * A data transfer object for communicating with the frontend, holds user-specific information.
 *
 * @param username the name of the user
 * @param team     the user's current team, this can be null
 * @param role     the user's current role, this can be null
 * @param isHost   whether the user is host
 */
public record PlayerDto(String username, Team team, Role role, boolean isHost) {
}
