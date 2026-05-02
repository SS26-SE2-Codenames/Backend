package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.utility.Color;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for Board. */
class BoardTest {
  private CardGenerator mockCardGenerator;
  private Board board;
  private List<Card> dummyCardList;

  @BeforeEach
  void setUp() {
    mockCardGenerator = mock(CardGenerator.class);

    Card card1 = new Card("Test1", Color.RED);
    Card card2 = new Card("Test2", Color.BLUE);
    Card card3 = new Card("Test3", Color.WHITE);
    Card card4 = new Card("Test4", Color.BLACK);

    dummyCardList = Arrays.asList(card1, card2, card3, card4);

    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(dummyCardList);

    board = new Board(mockCardGenerator, 4, 1, 1, 1, 1);
  }

  @Test
  void testConstructor() {
    assertEquals(4, board.getCardList().size());
    assertEquals(dummyCardList, board.getCardList());

    verify(mockCardGenerator, times(1)).generateCards(4, 1, 1, 1, 1);
  }

  @Test
  void testGetCardList() {
    assertEquals(dummyCardList, board.getCardList());
  }

  @Test
  void testCheckColor() {
    assertEquals(Color.RED, board.checkColor(0));
    assertEquals(Color.BLUE, board.checkColor(1));
    assertEquals(Color.WHITE, board.checkColor(2));
    assertEquals(Color.BLACK, board.checkColor(3));
  }

  @Test
  void testCheckColorNegative() {
    assertThrows(IllegalArgumentException.class, () -> board.checkColor(-1));
  }

  @Test
  void testCheckColorOutOfBounds() {
    assertThrows(IllegalArgumentException.class, () -> board.checkColor(5));
  }

  @Test
  void testIsGuessed() {
    assertFalse(board.getIsGuessed(0));
  }

  @Test
  void testSetIsGuessed() {
    board.setGuessed(0);
    assertTrue(board.getIsGuessed(0));
  }
}
