package com.codenames.codenames.backend.game.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket message for requesting a cheat check on selected cards.
 */
@Getter
@Setter
public class CheatCardMessage {
  private String lobbyCode;
  private String username;
  private List<Integer> positions;
}