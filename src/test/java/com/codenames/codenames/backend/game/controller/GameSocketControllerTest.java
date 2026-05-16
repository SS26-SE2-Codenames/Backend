package com.codenames.codenames.backend.game.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.game.dto.ClueMessage;
import com.codenames.codenames.backend.game.dto.GameStateDto;
import com.codenames.codenames.backend.game.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.dto.StartGameMessage;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** Tests websocket gameplay controller interactions. */
@ExtendWith(MockitoExtension.class)
class GameSocketControllerTest {

  @Mock private GameService gameService;

  @Mock private SimpMessagingTemplate messagingTemplate;

  private GameSocketController controller;

  @BeforeEach
  void setUp() {

    controller = new GameSocketController(gameService, messagingTemplate);
  }

  @Test
  void startGameShouldBroadcastState() {

    StartGameMessage message = new StartGameMessage();

    message.setLobbyCode("ABCDE");

    when(gameService.createGameStateDto("ABCDE")).thenReturn(mock(GameStateDto.class));

    controller.startGame(message);

    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void revealCardShouldBroadcastState() {

    RevealCardMessage message = new RevealCardMessage();

    message.setLobbyCode("ABCDE");
    message.setPosition(0);
    message.setCurrentTurn(Team.RED);

    when(gameService.createGameStateDto("ABCDE")).thenReturn(mock(GameStateDto.class));

    controller.revealCard(message);

    verify(gameService).flipCard("ABCDE", 0, Team.RED);

    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void submitClueShouldBroadcastState() {

    ClueMessage message = new ClueMessage();

    message.setLobbyCode("ABCDE");
    message.setWord("animal");
    message.setGuessAmount(2);
    message.setCurrentTurn(Team.RED);

    when(gameService.createGameStateDto("ABCDE")).thenReturn(mock(GameStateDto.class));

    controller.submitClue(message);

    verify(gameService).submitClue(anyString(), any(), any());

    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }
}
