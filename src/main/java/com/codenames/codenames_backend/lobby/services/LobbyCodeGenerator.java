package com.codenames.codenames_backend.lobby.services;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;

@Service
public class LobbyCodeGenerator {

  private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

  private static final int CODE_LENGTH = 5;
  private static final SecureRandom RANDOM = new SecureRandom();

  public String generateLobbyCode() {
    StringBuilder code = new StringBuilder(CODE_LENGTH);

    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = RANDOM.nextInt(CHARACTERS.length());
      code.append(CHARACTERS.charAt(index));
    }

    return code.toString();
  }
}
