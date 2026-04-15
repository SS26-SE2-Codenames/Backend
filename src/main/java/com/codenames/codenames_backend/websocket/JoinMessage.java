package com.codenames.codenames_backend.websocket;

import lombok.Getter;
import lombok.Setter;

/**
 * Message payload sent by clients to join a lobby.
 *
 * <p>Contains the player's name and the lobby code they want to join.
 */
@Getter
@Setter
public class JoinMessage {
  private String name;
  private String code;
}
