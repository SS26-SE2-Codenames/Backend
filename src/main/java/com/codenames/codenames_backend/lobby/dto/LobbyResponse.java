package com.codenames.codenames_backend.lobby.dto;

import lombok.Getter;

@Getter
public class LobbyResponse {
    private String message;
    private String lobbyCode;

    public LobbyResponse(String message, String lobbyCode) {
        this.message = message;
        this.lobbyCode = lobbyCode;
    }
}