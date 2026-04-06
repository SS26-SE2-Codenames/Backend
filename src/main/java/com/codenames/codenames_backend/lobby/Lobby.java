package com.codenames.codenames_backend.lobby;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Lobby {
    private static final int MAX_PLAYERS = 4;
    private final String lobbyCode;
    private final List<String> playerList;

    public Lobby(String lobbyCode, String username) {
        this.lobbyCode = lobbyCode;
        this.playerList = new ArrayList<>();
        this.addPlayer(username);
    }

    public void addPlayer(String username) {
        if (playerList.size() < MAX_PLAYERS) this.playerList.add(username);
    }

    public void removePlayer(String username) {
        this.playerList.remove(username);
    }
}
