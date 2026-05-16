package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.utility.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassTurnMessage {
  private String lobbyCode;
  private Team currentTurn;
}
