package com.codenames.codenames_backend.lobby.services;

import com.codenames.codenames_backend.lobby.Lobby;
import com.codenames.codenames_backend.lobby.Role;
import com.codenames.codenames_backend.lobby.Team;
import com.codenames.codenames_backend.websocket.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for managing lobbies and player interactions.
 *
 * <p>Handles creation of lobbies, player joins/leaves, and retrieval of lobby data. Ensures
 * uniqueness of lobby codes and thread-safe access to lobby storage.
 */

@Service
public class LobbyService {

    private final Map<String, Lobby> lobbyList = new ConcurrentHashMap<>();
    private final LobbyCodeGenerator generator;

    /**
     * Creates a new {@code LobbyService}.
     *
     * @param generator the lobby code generator used to create unique lobby codes
     */

    public LobbyService(LobbyCodeGenerator generator) {
        this.generator = generator;
    }

    /**
     * Creates a new lobby and adds the given user as the first player.
     *
     * @param username the username of the player creating the lobby
     * @return the generated lobby code, or {@code null} if creation fails
     */

    public String createLobby(String username) {
        String lobbyCode = generateLobbyCode();
        if (lobbyCode == null || lobbyCode.isBlank()) {
            return null;
        }

        Lobby lobby = new Lobby(lobbyCode, username);
        lobbyList.put(lobbyCode, lobby);
        return lobbyCode;
    }

    /**
     * Adds a player to an existing lobby.
     *
     * @param username the username of the player
     * @param lobbyCode the lobby code identifying the lobby
     * @return {@code true} if the player successfully joined, {@code false} otherwise
     */

    public boolean joinLobby(String username, String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        if (lobby != null) {
            return lobby.addPlayer(username);
        }
        return false;
    }

    /**
     * Removes a player from a lobby.
     *
     * @param username the username of the player
     * @param lobbyCode the lobby code identifying the lobby
     * @return {@code true} if the player was removed, {@code false} if the lobby does not exist
     */
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
        for (Player player : lobby.getPlayerList()) {
            if (!player.getUsername().equals(username)
                    && lobby.getPlayerTeam(player.getUsername()) == team
                    && lobby.getPlayerRole(player.getUsername()) == Role.SPYMASTER) {
                return true;
            }
        }
        return false;
    }

    private String generateLobbyCode() {
        String code = generator.generateLobbyCode();

        if (code == null || code.isBlank()) {
            return null;
        }

        while (lobbyList.containsKey(code)) {
            code = generator.generateLobbyCode();
            if (code == null || code.isBlank()) {
                return null;
            }
        }
        return code;
    }

    /**
     * Retrieves all players in the specified lobby.
     *
     * @param lobbyCode the lobby code identifying the lobby
     * @return a list of players, or an empty list if the lobby does not exist
     */
    public List<Player> getPlayers(String lobbyCode) {
        Lobby lobby = lobbyList.get(lobbyCode);
        return lobby != null ? lobby.getPlayerList() : List.of();
    }
}