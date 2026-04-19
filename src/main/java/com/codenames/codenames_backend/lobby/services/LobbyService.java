package com.codenames.codenames_backend.lobby.services;

import com.codenames.codenames_backend.lobby.Lobby;
import com.codenames.codenames_backend.lobby.Role;
import com.codenames.codenames_backend.lobby.Team;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LobbyService {

    private final HashMap<String, Lobby> lobbyList = new HashMap<>();
    private final LobbyCodeGenerator generator;

    public LobbyService(LobbyCodeGenerator generator) {
        this.generator = generator;
    }

    public String createLobby(String username) {
        String lobbyCode = generateLobbyCode();
        if (lobbyCode == null || lobbyCode.isBlank()) {
            return null;
        }

        Lobby lobby = new Lobby(lobbyCode, username);
        lobbyList.put(lobbyCode, lobby);
        return lobbyCode;
    }

    public boolean joinLobby(String username, String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        if (lobby != null) {
            lobby.addPlayer(username);
            return true;
        }
        return false;
    }

    public boolean leaveLobby(String username, String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        if (lobby != null) {
            lobby.removePlayer(username);
            return true;
        }
        return false;
    }

    public boolean selectPosition(String username, String lobbyCode, Team team, Role role) {
        Lobby lobby = lobbyList.get(lobbyCode);

        if (lobby == null || !lobby.hasPlayer(username)) {
            return false;
        }

        if (role == Role.SPYMASTER && isSpymasterAlreadyAssigned(lobby, username, team)) {
            return false;
        }

        lobby.setPlayerTeam(username, team);
        lobby.setPlayerRole(username, role);
        return true;
    }

    private boolean isSpymasterAlreadyAssigned(Lobby lobby, String username, Team team) {
        for (String player : lobby.getPlayerList()) {
            if (!player.equals(username)
                    && lobby.getPlayerTeam(player) == team
                    && lobby.getPlayerRole(player) == Role.SPYMASTER) {
                return true;
            }
        }
        return false;
    }

    private String generateLobbyCode() {
        String code = generator.generateLobbyCode();
        while (lobbyList.containsKey(code)) {
            code = generator.generateLobbyCode();
        }
        return code;
    }
}