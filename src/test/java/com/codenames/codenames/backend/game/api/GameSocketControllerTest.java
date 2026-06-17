package com.codenames.codenames.backend.game.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.chat.api.dto.ChatDto;
import com.codenames.codenames.backend.chat.api.dto.ChatMessageType;
import com.codenames.codenames.backend.database.persistence.PersistenceService;
import com.codenames.codenames.backend.game.api.dto.CheatCardMessage;
import com.codenames.codenames.backend.game.api.dto.ClueMessage;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.api.dto.PassTurnMessage;
import com.codenames.codenames.backend.game.api.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.api.dto.StartGameMessage;
import com.codenames.codenames.backend.game.application.CheatService;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.CheatResult;
import com.codenames.codenames.backend.game.domain.ExposeCheatResult;
import com.codenames.codenames.backend.lobby.domain.Team;
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

  @Mock private PersistenceService persistenceService;

  @Mock private CheatService cheatService;

  private GameSocketController controller;

  @BeforeEach
  void setUp() {
    controller =
        new GameSocketController(
            gameService,
            messagingTemplate,
            persistenceService,
            cheatService);
  }

  @Test
  void startGameShouldBroadcastStateWithoutPersisting() {
    StartGameMessage message = new StartGameMessage();
    message.setLobbyCode(LOBBY_CODE);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDto());

    controller.startGame(message);

    verify(persistenceService, never()).saveSnapShot(LOBBY_CODE);
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void revealCardShouldPersistAndBroadcastState() {
    RevealCardMessage message = new RevealCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setPosition(0);
    message.setCurrentTurn(Team.RED);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDto());

    controller.revealCard(message);

    verify(gameService).flipCard(LOBBY_CODE, 0, Team.RED);
    verify(persistenceService).saveSnapShot(LOBBY_CODE);
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
        .thenReturn(createGameStateDto());

    controller.submitClue(message);

    verify(gameService).submitClue(anyString(), any(), any());
    verify(persistenceService).saveSnapShot(LOBBY_CODE);
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void passTurnShouldPersistAndBroadcastUpdatedState() {
    PassTurnMessage message = new PassTurnMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setCurrentTurn(Team.RED);

    when(gameService.getCurrentGameState(LOBBY_CODE))
        .thenReturn(createGameStateDto());

    controller.passTurn(message);

    verify(gameService).passTurn(LOBBY_CODE, Team.RED);
    verify(persistenceService).saveSnapShot(LOBBY_CODE);
    verify(messagingTemplate).convertAndSend(anyString(), any(Object.class));
  }

  private GameStateDto createGameStateDto() {
    return new GameStateDto(null, Team.RED, null, null, 0,  List.of(), false, false);
  }

  @Test
  void useCheatShouldSendPrivateSystemMessageAndPersistSnapshot() {
    CheatCardMessage message = new CheatCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setUsername("Max");
    message.setPositions(List.of(0, 1));

    CheatResult result = new CheatResult("Die Karte \"Dog\" ist richtig.", Team.RED);

    when(cheatService.useCheat(LOBBY_CODE, "Max", List.of(0, 1))).thenReturn(result);

    controller.useCheat(message);

    verify(cheatService).useCheat(LOBBY_CODE, "Max", List.of(0, 1));

    verify(messagingTemplate)
        .convertAndSend(
            "/topic/chat/" + LOBBY_CODE + "/RED/operative",
            new ChatDto("System", result.message(), ChatMessageType.SYSTEM));

    verify(persistenceService).saveSnapShot(LOBBY_CODE);
  }

  @Test
  void useCheatShouldDoNothingWhenResultIsNull() {
    CheatCardMessage message = new CheatCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setUsername("Max");
    message.setPositions(List.of(0, 1));

    when(cheatService.useCheat(LOBBY_CODE, "Max", List.of(0, 1))).thenReturn(null);

    controller.useCheat(message);

    verify(cheatService).useCheat(LOBBY_CODE, "Max", List.of(0, 1));
    verify(messagingTemplate, never())
        .convertAndSendToUser(anyString(), anyString(), any(Object.class));
    verify(persistenceService, never()).saveSnapShot(LOBBY_CODE);
  }

  @Test
  void exposeCheatShouldPersistAndBroadcastUpdatedState() {
    CheatCardMessage message = new CheatCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setUsername("Max");
    message.setPositions(List.of());
    GameStateDto gameState = createGameStateDto();

    when(cheatService.exposeCheat(LOBBY_CODE, "Max"))
        .thenReturn(new ExposeCheatResult(true, Team.RED));
    when(gameService.getCurrentGameState(LOBBY_CODE)).thenReturn(gameState);

    controller.exposeCheat(message);

    verify(cheatService).exposeCheat(LOBBY_CODE, "Max");
    verify(messagingTemplate)
        .convertAndSend(
            "/topic/chat/" + LOBBY_CODE + "/RED/operative",
            new ChatDto("System", "EXPOSE_CORRECT", ChatMessageType.SYSTEM));
    verify(persistenceService).saveSnapShot(LOBBY_CODE);
    verify(messagingTemplate).convertAndSend("/topic/game/" + LOBBY_CODE, gameState);
  }

  @Test
  void exposeCheatShouldSendWrongSystemMessage() {
    CheatCardMessage message = new CheatCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setUsername("Max");
    message.setPositions(List.of());
    GameStateDto gameState = createGameStateDto();

    when(cheatService.exposeCheat(LOBBY_CODE, "Max"))
        .thenReturn(new ExposeCheatResult(false, Team.BLUE));
    when(gameService.getCurrentGameState(LOBBY_CODE)).thenReturn(gameState);

    controller.exposeCheat(message);

    verify(messagingTemplate)
        .convertAndSend(
            "/topic/chat/" + LOBBY_CODE + "/BLUE/operative",
            new ChatDto("System", "EXPOSE_WRONG", ChatMessageType.SYSTEM));
    verify(messagingTemplate).convertAndSend("/topic/game/" + LOBBY_CODE, gameState);
  }

  @Test
  void exposeCheatShouldDoNothingWhenResultIsNull() {
    CheatCardMessage message = new CheatCardMessage();
    message.setLobbyCode(LOBBY_CODE);
    message.setUsername("Max");
    message.setPositions(List.of());

    when(cheatService.exposeCheat(LOBBY_CODE, "Max")).thenReturn(null);

    controller.exposeCheat(message);

    verify(cheatService).exposeCheat(LOBBY_CODE, "Max");
    verify(persistenceService, never()).saveSnapShot(LOBBY_CODE);
    verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
  }
}
