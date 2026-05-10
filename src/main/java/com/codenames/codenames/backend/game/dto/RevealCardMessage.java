package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.utility.Color;
import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket message for revealing a card on the board.
 *
 * <p>Contains the lobby code, selected card position, and the current team's turn color.
 */
@Getter
@Setter
public class RevealCardMessage {
  private String lobbyCode;
  private int position;
  private Color currentTurn;
}
