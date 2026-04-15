package com.codenames.codenames_backend.websocket;

import lombok.Getter;

/** Represents a player connected via WebSocket. */
@Getter
public class Player {
  private final String username;

  public Player(String username) {
    this.username = username;
  }
}
