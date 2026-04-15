package com.codenames.codenames_backend.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinMessage {
  private String name;
  private String code;
}
