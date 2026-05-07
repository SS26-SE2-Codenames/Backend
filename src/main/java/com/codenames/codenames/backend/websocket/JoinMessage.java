package com.codenames.codenames.backend.websocket;

import lombok.Getter;
import lombok.Setter;

/**
 * Message payload sent by clients to join a lobby.
 *
 * <p>Contains the player's username and the target lobby code.
 */
@Getter
@Setter
public class JoinMessage {
  private String name;
  private String code;
}
