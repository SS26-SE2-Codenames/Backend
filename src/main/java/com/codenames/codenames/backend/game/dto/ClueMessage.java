package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.utility.Team;
import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket message for submitting a clue.
 *
 * <p>Contains the clue word and the allowed amount of guesses.
 */
@Getter
@Setter
public class ClueMessage {
  private String lobbyCode;
  private String word;
  private int guessAmount;
  private Team currentTurn;
}
