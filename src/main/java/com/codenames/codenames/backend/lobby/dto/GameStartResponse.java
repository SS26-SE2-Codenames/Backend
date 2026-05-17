package com.codenames.codenames.backend.lobby.dto;

import java.util.List;

/**
 * This response is transferred, when the host of a lobby requests a game start.
 * It is used as a broadcast message type which all players receive simultaneously.
 *
 * @param message the message that is displayed
 * @param lobbyCode the lobbyCode of the starting lobby
 * @param playerList list of all players including their roles
 * @param isGameStarted if the game was started successfully
 */
public record GameStartResponse(String message, String lobbyCode, List<PlayerDto> playerList, boolean isGameStarted) {
}
