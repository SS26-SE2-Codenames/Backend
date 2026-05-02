package com.codenames.codenames.backend.lobby.dto;

import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for selecting a position (team and role) in a lobby.
 */
@Getter
@Setter
@NoArgsConstructor
public class PositionSelectMessage {

  private String username;
  private String lobbyCode;
  private Team team;
  private Role role;
}