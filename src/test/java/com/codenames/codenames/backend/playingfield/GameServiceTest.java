package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.game.dto.ClueDto;
import com.codenames.codenames.backend.serialization.CardDataTransferObject;
import com.codenames.codenames.backend.serialization.DataTransferObjectService;
import com.codenames.codenames.backend.serialization.GameStateDataTransferObject;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the functionality of GameService. */
class GameServiceTest {
  private GameService gameService;
  private GameManager mockGameManager;
  private GameManagerFactory mockGameManagerFactory;
  private DataTransferObjectService mockDtoService;

  private final String lobbyCode = "ABCDE";
  private final Team redTeam = Team.RED;

  @BeforeEach
  void setup() {
    mockGameManagerFactory = mock(GameManagerFactory.class);
    mockGameManager = mock(GameManager.class);
    mockDtoService = mock(DataTransferObjectService.class);

    gameService = new GameService(mockGameManagerFactory, mockDtoService);
    when(mockGameManagerFactory.create(redTeam)).thenReturn(mockGameManager);

    gameService.createGameManager(lobbyCode, redTeam);
  }

  @Test
  void testCreateGameManager_oneInvocation() {
    verify(mockGameManagerFactory, times(1)).create(redTeam);
  }

  @Test
  void testCreateGameManager_twoInvocations_noDuplicates() {
    gameService.createGameManager(lobbyCode, redTeam);
    gameService.createGameManager(lobbyCode, redTeam);
    verify(mockGameManagerFactory, times(1)).create(redTeam);
  }

  @Test
  void testRemoveGame() {
    gameService.removeGame(lobbyCode);

    assertThrows(IllegalStateException.class, () -> gameService.flipCard(lobbyCode, 0, redTeam));
  }

  @Test
  void testSubmitClue() {
    Clue mockClue = new Clue("ANIMAL", 2);

    gameService.submitClue(lobbyCode, mockClue, redTeam);
    verify(mockGameManager, times(1)).submitClue(mockClue, redTeam);
  }

  @Test
  void testFlipCard() {
    int firstCard = 0;
    gameService.flipCard(lobbyCode, firstCard, redTeam);

    verify(mockGameManager, times(1)).flipCard(firstCard, redTeam);
  }

  @Test
  void testPassTurn() {
    gameService.passTurn(lobbyCode, redTeam);

    verify(mockGameManager, times(1)).passTurn(redTeam);
  }

  @Test
  void testGetGameState() {

    GameManager result = gameService.getGameState(lobbyCode);

    assertEquals(mockGameManager, result);
  }

  @Test
  void testRestoreGameManager() {
    String restoredLobbyCode = "VWXYZ";
    GameManager restoredGameManager = mock(GameManager.class);

    gameService.restoreGameManager(restoredLobbyCode, restoredGameManager);

    assertEquals(restoredGameManager, gameService.getGameState(restoredLobbyCode));
  }

  @Test
  void testGetCurrentGameState() {
    GameStateDataTransferObject expected =
        new GameStateDataTransferObject(
            null,
            redTeam,
            Role.SPYMASTER,
            new ClueDto("ANIMAL", 2),
            2,
            List.of(new CardDataTransferObject("Dog", Color.RED, false)));
    when(mockGameManager.getCurrentTurn()).thenReturn(redTeam);
    when(mockGameManager.getCurrentPhase()).thenReturn(Role.SPYMASTER);
    when(mockGameManager.getRemainingGuesses()).thenReturn(2);
    when(mockDtoService.createGameStateDataTransferObject(mockGameManager, redTeam, Role.SPYMASTER))
        .thenReturn(expected);

    GameStateDataTransferObject result = gameService.getCurrentGameState(lobbyCode);

    assertEquals(expected, result);
  }

  @Test
  void testIsGameStartedWhenGameExists() {
    assertTrue(gameService.isGameStarted(lobbyCode));
  }

  @Test
  void testIsGameStartedWhenGameDoesNotExist() {
    assertFalse(gameService.isGameStarted("UNKNOWN"));
  }

  @Test
  void getGameSnapshotsShouldReturnAllCurrentGameStates() {
    GameStateDataTransferObject expected =
        new GameStateDataTransferObject(
            null,
            redTeam,
            Role.SPYMASTER,
            new ClueDto("ANIMAL", 2),
            2,
            List.of(new CardDataTransferObject("Dog", Color.RED, false)));

    when(mockGameManager.getCurrentTurn()).thenReturn(redTeam);
    when(mockGameManager.getCurrentPhase()).thenReturn(Role.SPYMASTER);
    when(mockDtoService.createGameStateDataTransferObject(mockGameManager, redTeam, Role.SPYMASTER))
        .thenReturn(expected);

    Map<String, GameStateDataTransferObject> snapshots = gameService.getGameSnapshots();

    assertEquals(1, snapshots.size());
    assertEquals(expected, snapshots.get(lobbyCode));
  }
}
