package com.codenames.codenames_backend.lobby.dto;

import lombok.Getter;
import com.codenames.codenames_backend.lobby.Team;
import com.codenames.codenames_backend.lobby.Role;

@Getter
public class LobbyResponse {
    private String message;
    private String lobbyCode;
    private Team team;
    private Role role;

    // ALTER Konstruktor
    public LobbyResponse(String message, String lobbyCode) {
        this.message = message;
        this.lobbyCode = lobbyCode;
    }

    // NEUER Konstruktor
    public LobbyResponse(String message, String lobbyCode, Team team, Role role) {
        this.lobbyCode = lobbyCode;
        this.message = message;
        this.team = team;
        this.role = role;
    }
}
