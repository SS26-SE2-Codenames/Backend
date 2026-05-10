package com.codenames.codenames.backend.game.controller;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class GameSocketControllerTest {
  @Mock private LobbyService lobbyService;

  @Mock private SimpMessagingTemplate messagingTemplate;

  @Mock private CardGenerator cardGenerator;

  @Mock private ClueValidationService clueValidationService;

  private GameSocketController controller;

  @BeforeEach
  void setUp() {
    controller =
        new GameSocketController(
            lobbyService, messagingTemplate, cardGenerator, clueValidationService);
  }
}
