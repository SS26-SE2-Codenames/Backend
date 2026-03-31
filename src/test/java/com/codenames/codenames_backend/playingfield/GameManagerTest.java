package com.codenames.codenames_backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for GameManager. */
public class GameManagerTest {
  private CardGenerator mockCardGenerator;

  @BeforeEach
  void setUp() {
    mockCardGenerator = mock(CardGenerator.class);
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(new ArrayList<>());
  }

  @Test
  void testConstructorRedStarts() {
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    verify(mockCardGenerator, times(1)).generateCards(25, 9, 8, 7, 1);
  }

  @Test
  void testConstructorBlueStarts() {
    GameManager gameManager = new GameManager(Color.BLUE, mockCardGenerator);
    verify(mockCardGenerator, times(1)).generateCards(25, 8, 9, 7, 1);
  }

  @Test
  void testConstructorNullStarts() {
    assertThrows(IllegalArgumentException.class, () -> new GameManager(null, mockCardGenerator));
  }

  @Test
  void testConstructorWrongColorStarts() {
    assertThrows(
        IllegalArgumentException.class, () -> new GameManager(Color.WHITE, mockCardGenerator));
    assertThrows(
        IllegalArgumentException.class, () -> new GameManager(Color.BLACK, mockCardGenerator));
  }

  @Test
  void testGetCardList() {
    Card card1 = new Card("Test", Color.RED);
    List<Card> cardList = List.of(card1);

    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    assertEquals(1, gameManager.getCardList().size());
  }

  @Test
  void testCheckColor() {
    Card card1 = new Card("Test", Color.RED);
    List<Card> cardList = List.of(card1);

    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    assertEquals(Color.RED, gameManager.checkColor(0));
  }
}
