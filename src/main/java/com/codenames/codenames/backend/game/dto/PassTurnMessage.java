package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.lobby.domain.Team;
import lombok.Getter;
import lombok.Setter;

/**
 * Message used for requesting an early turn pass.
 *
 * <p>Contains the lobby code and team initiating the action.
 */
@Getter
@Setter
public class PassTurnMessage {
  private String lobbyCode;
  private Team currentTurn;
}
