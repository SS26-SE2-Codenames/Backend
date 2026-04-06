package com.codenames.codenames_backend.lobby.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LobbyResponse {
    private String message;
    private String lobbyCode;

    public LobbyResponse(String message, String lobbyCode) {
        this.lobbyCode = lobbyCode;
        this.message = message;
    }
}
