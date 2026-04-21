package com.codenames.codenames_backend.websocket;

import lombok.Getter;

/**
 * Represents a player connected via WebSocket.
 *
 * <p>A player is identified by a username and is associated with a WebSocket session.
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
