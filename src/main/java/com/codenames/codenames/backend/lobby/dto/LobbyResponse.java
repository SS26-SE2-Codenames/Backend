package com.codenames.codenames.backend.lobby.dto;

import com.codenames.codenames.backend.websocket.Player;

import java.util.List;

/**
 * Data transfer object representing the result of a lobby operation.
 *
 * <p>Contains a message describing the outcome and the associated lobby code.
 */
public record LobbyResponse(String message, String lobbyCode, List<Player> playerList) {
  /**
   * Creates a new lobby response.
   *
   * @param message   the message describing the result of the operation
   * @param lobbyCode the associated lobby code
   * @param playerList   the list of playerList currently in the lobby
   */
  public LobbyResponse {
  }
}
