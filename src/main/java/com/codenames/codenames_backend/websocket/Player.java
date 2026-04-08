package com.codenames.codenames_backend.websocket;

import org.springframework.web.socket.WebSocketSession;

/** Represents a player connected via WebSocket. */
public class Player {
  private final String username;
  private WebSocketSession session;

  /**
   * Creates a new player.
   *
   * @param username the player's username
   * @param session the WebSocket session of the player
   */
  public Player(String username, WebSocketSession session) {
    this.username = username;
    this.session = session;
  }

  /**
   * Returns the username of the player.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the WebSocket session of the player.
   *
   * @return the session
   */
  public WebSocketSession getSession() {
    return session;
  }

  /**
   * Sets the active {@link WebSocketSession} for this instance.
   *
   * @param session the WebSocket session to assign
   */
  public void setSession(WebSocketSession session) {
    this.session = session;
  }
}
