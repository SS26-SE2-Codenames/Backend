package com.codenames.codenames.backend.lobby.dto;

import java.util.List;

/**
 * Data transfer object representing the result of a lobby operation.
 *
 * <p>Contains a message describing the outcome and the associated lobby code.
 */
public record LobbyResponse(String message, String lobbyCode, List<PlayerDto> playerList, boolean isStarted) {
}
