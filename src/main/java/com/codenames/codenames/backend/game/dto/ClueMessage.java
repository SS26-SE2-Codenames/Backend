package com.codenames.codenames.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClueMessage {
  private String lobbyCode;
  private String word;
  private int guessAmount;
}
