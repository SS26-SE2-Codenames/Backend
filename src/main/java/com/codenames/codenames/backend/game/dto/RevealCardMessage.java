package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.utility.Color;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevealCardMessage {
  private String lobbyCode;
  private int position;
  private Color currentTurn;
}
