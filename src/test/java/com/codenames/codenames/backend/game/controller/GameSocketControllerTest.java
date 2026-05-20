package com.codenames.codenames.backend.game.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.game.dto.ClueMessage;
import com.codenames.codenames.backend.game.dto.PassTurnMessage;
import com.codenames.codenames.backend.game.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.dto.StartGameMessage;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.recovery.SystemStatePersistenceService;
import com.codenames.codenames.backend.serialization.GameStateDataTransferObject;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** Tests websocket gameplay controller interactions. */
@ExtendWith(MockitoExtension.class)
class GameSocketControllerTest {

  private static final String LOBBY_CODE = "ABCDE";

  @Mock private GameService gameService;

  @Mock private SimpMessagingTemplate messagingTemplate;

  @Mock private SystemStatePersistenceService persistenceService;

  private GameSocketController controller;

  @BeforeEach
  void setUp() {
    controller = new GameSocketController(gameService, messagingTemplate, persistenceService);
  }

  @Test
  void startGameShouldBroadcastStateWithoutPersisting() {
    StartGameMessage message = new StartGameMessage();
    message.setLobbyCode(LOBBY_CODE);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDataTransferObject());

    controller.startGame(message);

    verify(persistenceService, never()).persistCurrentState();
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void revealCardShouldPersistAndBroadcastState() {
    RevealCardMessage message = new RevealCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setPosition(0);
    message.setCurrentTurn(Team.RED);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDataTransferObject());

    controller.revealCard(message);

    verify(gameService).flipCard(LOBBY_CODE, 0, Team.RED);
    verify(persistenceService).persistCurrentState();
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void submitClueShouldPersistAndBroadcastState() {
    ClueMessage message = new ClueMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setWord("animal");
    message.setGuessAmount(2);
    message.setCurrentTurn(Team.RED);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDataTransferObject());

    controller.submitClue(message);

    verify(gameService).submitClue(anyString(), any(), any());
    verify(persistenceService).persistCurrentState();
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void passTurnShouldPersistAndBroadcastUpdatedState() {
    PassTurnMessage message = new PassTurnMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setCurrentTurn(Team.RED);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDataTransferObject());

    controller.passTurn(message);

    verify(gameService).passTurn(LOBBY_CODE, Team.RED);
    verify(persistenceService).persistCurrentState();
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  private GameStateDataTransferObject createGameStateDataTransferObject() {
    return new GameStateDataTransferObject(null, Team.RED, null, null, List.of());
  }
}
