package com.codenames.codenames_backend.lobby.dto;

import com.codenames.codenames_backend.utility.Role;
import com.codenames.codenames_backend.utility.Team;
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