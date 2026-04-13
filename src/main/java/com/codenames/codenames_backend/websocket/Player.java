package com.codenames.codenames_backend.websocket;


/** Represents a player connected via WebSocket. */
public class Player {
  private final String username;

  /**
   * Creates a new player.
   *
   * @param username the player's username
   * @param session the WebSocket session of the player
   */
  public Player(String username) {
    this.username = username;
  }

  /**
   * Returns the username of the player.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }
}
