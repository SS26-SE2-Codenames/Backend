package com.codenames.codenames_backend.websocket;

import org.springframework.web.socket.WebSocketSession;

public class Player {
  private String username;
  private WebSocketSession session;

  public Player(String username, WebSocketSession session) {
    this.username = username;
    this.session = session;
  }

  public String getUsername() {
    return username;
  }

  public WebSocketSession getSession() {
    return session;
  }
}
