package com.codenames.codenames_backend.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames_backend.playingfield.Card;
import com.codenames.codenames_backend.playingfield.Color;
import com.codenames.codenames_backend.playingfield.GameManager;
import com.codenames.codenames_backend.serialization.DataTransferObjectService.Role;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataTransferObjectServiceTest {
  Card card;
  GameManager mockGameManager;
  DataTransferObjectService service;
  GameStateDataTransferObject gameStateDto;

  @BeforeEach
  public void setUp() {
    card = new Card("Test1", Color.RED);

    mockGameManager = mock(GameManager.class);
    service = new DataTransferObjectService();
    when(mockGameManager.getCardList()).thenReturn(List.of(card));
    when(mockGameManager.getWinner()).thenReturn(Color.RED);
    when(mockGameManager.getCurrentRedFound()).thenReturn(0);
    when(mockGameManager.getCurrentBlueFound()).thenReturn(0);
  }

  @Test
  void testSpymasterVisibility() {
    gameStateDto =
        service.createGameStateDataTransferObject(
            mockGameManager, DataTransferObjectService.Role.SPYMASTER, "RED");
    assertEquals("RED", gameStateDto.cardList().get(0).color());
  }

  @Test
  void testOperatorVisibility() {
    gameStateDto =
        service.createGameStateDataTransferObject(mockGameManager, Role.OPERATIVE, "RED");
    assertEquals("HIDDEN", gameStateDto.cardList().get(0).color());
  }
}
