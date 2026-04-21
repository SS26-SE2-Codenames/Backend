package com.codenames.codenames_backend.lobby.dto;

import com.codenames.codenames_backend.lobby.Role;
import com.codenames.codenames_backend.lobby.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PositionSelectMessage {
    private String username;
    private String lobbyCode;
    private Team team;
    private Role role;
}