package com.codenames.codenames_backend.lobby;

import com.codenames.codenames_backend.websocket.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Lobby {
    private static final int MAX_PLAYERS = 4;
    private final String lobbyCode;
    private final List<Player> playerList = new CopyOnWriteArrayList<>();

    public Lobby(String lobbyCode, String username) {
        this.lobbyCode = lobbyCode;
        this.addPlayer(username);
    }

    public void addPlayer(String username) {
        boolean alreadyExists = playerList.stream()
                .anyMatch(p -> p.getUsername().equals(username));

        if (!alreadyExists && playerList.size() < MAX_PLAYERS) {
            playerList.add(new Player(username));
        }
    }

    public void removePlayer(String username) {
        playerList.removeIf(p -> p.getUsername().equals(username));
    }
}
