package com.codenames.codenames.backend.game.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket message for starting a game session.
 *
 * <p>Contains the lobby code of the game that should be started.
 */
@Getter
@Setter
public class StartGameMessage {
  private String lobbyCode;
}
