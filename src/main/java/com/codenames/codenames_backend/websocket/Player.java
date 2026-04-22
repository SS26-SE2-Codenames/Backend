package com.codenames.codenames_backend.websocket;

import lombok.Getter;

/**
 * Represents a player connected to the system.
 *
 * <p>A player is identified by a username and may be associated with a WebSocket session.
 */
@Getter
public class Player {
  private final String username;

  /**
   * Creates a new player.
   *
   * @param username the player's username
   */
  public Player(String username) {
    this.username = username;
  }
}
