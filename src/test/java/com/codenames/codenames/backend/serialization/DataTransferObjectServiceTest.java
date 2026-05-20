package com.codenames.codenames.backend.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataTransferObjectServiceTest {
  Card cardHidden;
  Card cardGuessed;
  GameManager mockGameManager;
  DataTransferObjectService service;
  GameStateDataTransferObject gameStateDto;
  private static final Team redTeam = Team.RED;
  private static final Role spymaster = Role.SPYMASTER;
  private static final Role operative = Role.OPERATIVE;

  @BeforeEach
  void setUp() {
    cardHidden = new Card("Test1", Color.RED);
    cardGuessed = new Card("Test1", Color.RED);
    cardGuessed.setIsGuessedTrue();

    mockGameManager = mock(GameManager.class);
    service = new DataTransferObjectService();
    when(mockGameManager.getCardList()).thenReturn(List.of(cardHidden, cardGuessed));
    when(mockGameManager.getWinner()).thenReturn(Team.RED);
    when(mockGameManager.getCurrentRedFound()).thenReturn(0);
    when(mockGameManager.getCurrentBlueFound()).thenReturn(0);
    when(mockGameManager.getRemainingGuesses()).thenReturn(2);

    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, redTeam, spymaster);
  }

  @Test
  void testSpymasterVisibility() {
    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, redTeam, spymaster);
    assertEquals(Color.RED, gameStateDto.cardList().get(0).color());
  }

  @Test
  void testOperatorVisibility_hidden() {
    assertEquals(Color.RED, gameStateDto.cardList().get(0).color());
  }

  @Test
  void testOperatorVisibility_isGuessed() {
    assertEquals(Color.RED, gameStateDto.cardList().get(1).color());
  }

  @Test
  void testGetWinner_exists() {
    assertEquals(redTeam, gameStateDto.winner());
  }

  @Test
  void testGetWinner_null() {
    when(mockGameManager.getWinner()).thenReturn(null);
    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, redTeam, operative);
    assertNull(gameStateDto.winner());
  }

  @Test
  void testCreateGameStateDataTransferObject() {
    Clue clue = new Clue("word", 1);
    when(mockGameManager.getCardList()).thenReturn(List.of());
    when(mockGameManager.getWinner()).thenReturn(null);
    when(mockGameManager.getCurrentClue()).thenReturn(clue);
    when(mockGameManager.getRemainingGuesses()).thenReturn(3);

    GameStateDataTransferObject dto =
        service.createGameStateDataTransferObject(mockGameManager, redTeam, operative);

    assertEquals(clue.word(), dto.currentClue().word());
    assertEquals(clue.guessAmount(), dto.currentClue().guessAmount());
    assertNull(dto.winner());
    assertEquals(0, dto.cardList().size());
    assertEquals(redTeam, dto.currentTurn());
    assertEquals(operative, dto.currentPhase());
  }
}
