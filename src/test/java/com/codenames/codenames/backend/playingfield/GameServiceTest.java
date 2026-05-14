package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServiceTest {
  private GameService gameService;
  private GameManager mockGameManager;
  private GameManagerFactory mockGameManagerFactory;

  private final String lobbyCode = "ABCDE";

  @BeforeEach
  void setup() {
    mockGameManagerFactory = mock(GameManagerFactory.class);
    mockGameManager = mock(GameManager.class);

    gameService = new GameService(mockGameManagerFactory);
    when(mockGameManagerFactory.create(Team.RED)).thenReturn(mockGameManager);

    gameService.createGameManager(lobbyCode, Team.RED);
  }

  @Test
  void testCreateGameManager_oneInvocation() {
    verify(mockGameManagerFactory, times(1)).create(Team.RED);
  }

  @Test
  void testCreateGameManager_twoInvocations_noDuplicates() {
    gameService.createGameManager(lobbyCode, Team.RED);
    gameService.createGameManager(lobbyCode, Team.RED);
    verify(mockGameManagerFactory, times(1)).create(Team.RED);
  }

  @Test
  void testRemoveGame() {
    gameService.removeGame(lobbyCode);

    assertThrows(IllegalStateException.class, () -> gameService.flipCard(lobbyCode, 0, Team.RED));
  }

  @Test
  void testSubmitClue() {
    Clue mockClue = mock(Clue.class);

    gameService.submitClue(lobbyCode, mockClue, Team.RED);
    verify(mockGameManager, times(1)).submitClue(mockClue, Team.RED);
    verify(mockGameManager, times(1)).advanceTurn();
  }

  @Test
  void testFlipCard() {
    gameService.flipCard(lobbyCode, 0, Team.RED);

    verify(mockGameManager, times(1)).flipCard(0, Team.RED);
  }

  @Test
  void testPassTurn() {
    gameService.passTurn(lobbyCode, Team.RED);

    verify(mockGameManager, times(1)).passTurn(Team.RED);
  }
}
