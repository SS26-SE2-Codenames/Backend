package com.codenames.codenames_backend.lobby;

import com.codenames.codenames_backend.websocket.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Lobby {
    private static final int MAX_PLAYERS = 4;
    private final String lobbyCode;
    private final List<Player> playerList;

    // username -> selected team
    private final Map<String, Team> playerTeams;

    // username -> selected role
    private final Map<String, Role> playerRoles;

    public Lobby(String lobbyCode, String username) {
        this.lobbyCode = lobbyCode;
        this.playerList = new ArrayList<>();
        this.playerTeams = new HashMap<>();
        this.playerRoles = new HashMap<>();
        this.addPlayer(username);
    }

    public void addPlayer(String username) {
        if (playerList.size() < MAX_PLAYERS && !hasPlayer(username)) {
            playerList.add(new Player(username));
        }
    }

    public void removePlayer(String username) {
        playerList.removeIf(p -> p.getUsername().equals(username));
        this.playerTeams.remove(username);
        this.playerRoles.remove(username);
    }

    public boolean hasPlayer(String username) {
        return playerList.stream()
                .anyMatch(p -> p.getUsername().equals(username));
    }

    public void setPlayerTeam(String username, Team team) {
        playerTeams.put(username, team);
    }

    public void setPlayerRole(String username, Role role) {
        playerRoles.put(username, role);
    }

    public Team getPlayerTeam(String username) {
        return playerTeams.get(username);
    }

    public Role getPlayerRole(String username) {
        return playerRoles.get(username);
    }
}
