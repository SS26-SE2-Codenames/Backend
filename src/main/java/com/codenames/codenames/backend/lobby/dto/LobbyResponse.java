package com.codenames.codenames.backend.lobby.dto;

import lombok.Getter;

/**
 * Data transfer object representing the result of a lobby operation.
 *
 * <p>Contains a message describing the outcome and the associated lobby code.
 */
@Getter
public class LobbyResponse {
  private final String message;
  private final String lobbyCode;

  /**
   * Creates a new lobby response.
   *
   * @param message the message describing the result of the operation
   * @param lobbyCode the associated lobby code
   */
  public LobbyResponse(String message, String lobbyCode) {
    this.lobbyCode = lobbyCode;
    this.message = message;
  }
}
