package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.serialization.DataTransferObjectService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.game.dto.GameStateDto;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the functionality of GameService. */
class GameServiceTest {
  private GameService gameService;
  private GameManager mockGameManager;
  private GameManagerFactory mockGameManagerFactory;

  private final String lobbyCode = "ABCDE";
  private final Team redTeam = Team.RED;

  @BeforeEach
  void setup() {
    mockGameManagerFactory = mock(GameManagerFactory.class);
    mockGameManager = mock(GameManager.class);
    DataTransferObjectService mockDtoService = mock(DataTransferObjectService.class);

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
    Clue mockClue = mock(Clue.class);

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
  void testCreateGameStateDto() {

    when(mockGameManager.getCardList()).thenReturn(List.of());

    GameStateDto dto = gameService.createGameStateDto(lobbyCode);

    assertNotNull(dto);
  }
}
