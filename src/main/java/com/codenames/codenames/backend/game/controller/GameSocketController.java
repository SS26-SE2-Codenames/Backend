package com.codenames.codenames.backend.game.controller;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class GameSocketController {

  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;
  private final CardGenerator cardGenerator;
  private final ClueValidationService clueValidationService;

  public GameSocketController(
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate,
      CardGenerator cardGenerator,
      ClueValidationService clueValidationService) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
    this.cardGenerator = cardGenerator;
    this.clueValidationService = clueValidationService;
  }
}
