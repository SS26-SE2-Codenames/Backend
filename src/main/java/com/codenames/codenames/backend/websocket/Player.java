package com.codenames.codenames.backend.websocket;

/**
 * Represents a player connected to the system.
 *
 * <p>A player is identified by a username and may be associated with a WebSocket session.
 */
public record Player(String username, boolean isHost) {
  /**
   * Creates a new player.
   *
   * @param username the player's username
   */
  public Player {
  }
}
