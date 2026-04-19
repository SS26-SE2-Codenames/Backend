package com.codenames.codenames_backend.lobby.services;

import com.codenames.codenames_backend.lobby.Lobby;
import com.codenames.codenames_backend.websocket.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyService {

    private final Map<String, Lobby> lobbyList = new ConcurrentHashMap<>();
    private final LobbyCodeGenerator generator;

    public LobbyService(LobbyCodeGenerator generator) {
        this.generator = generator;
    }

    public String createLobby(String username) {
        String lobbyCode = generateLobbyCode();
        if (lobbyCode == null || lobbyCode.isBlank()) return null;
        Lobby lobby = new Lobby(lobbyCode, username);
        lobbyList.put(lobbyCode, lobby);
        return lobbyCode;
    }

    public boolean joinLobby(String username, String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        if (lobby != null) {
            lobby.addPlayer(username);
            return true;
        } else {
            return false;
        }
    }

    public boolean leaveLobby(String username, String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        if (lobby != null) {
            lobby.removePlayer(username);
            return true;
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

    public List<Player> getPlayers(String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        return lobby != null ? lobby.getPlayerList() : List.of();
    }
}
