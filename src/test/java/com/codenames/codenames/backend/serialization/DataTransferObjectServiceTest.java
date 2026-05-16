package com.codenames.codenames.backend.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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

    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, operative, redTeam, spymaster);
  }

  @Test
  void testSpymasterVisibility() {
    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, spymaster, redTeam, spymaster);
    assertEquals("RED", gameStateDto.cardList().get(0).color());
  }

  @Test
  void testOperatorVisibility_hidden() {
    assertEquals("HIDDEN", gameStateDto.cardList().get(0).color());
  }

  @Test
  void testOperatorVisibility_isGuessed() {
    assertEquals("RED", gameStateDto.cardList().get(1).color());
  }

  @Test
  void testGetWinner_exists() {
    assertEquals(redTeam, gameStateDto.winner());
  }

  @Test
  void testGetWinner_null() {
    when(mockGameManager.getWinner()).thenReturn(null);
    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, operative, redTeam, spymaster);
    assertNull(gameStateDto.winner());
  }
}
