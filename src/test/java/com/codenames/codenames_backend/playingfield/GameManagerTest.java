package com.codenames.codenames_backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;
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

  @Test
  void testGetWinner_null() {
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    assertNull(gameManager.getWinner());
  }

  // Helper method for testing permutation of getWinner()
  private @NonNull GameManager helperMethodGenerateFullCardList(
      Color cardColor, Color startingTeam) {
    List<Card> cardList = new ArrayList<>();
    for (int i = 0; i < 25; i++) {
      cardList.add(new Card("Test" + i, cardColor));
    }
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);

    GameManager gameManager = new GameManager(startingTeam, mockCardGenerator);
    return gameManager;
  }

  @Test
  void testGetWinner_redStartsRedWins() {
    GameManager gameManager = helperMethodGenerateFullCardList(Color.RED, Color.RED);

    for (int i = 0; i < 9; i++) {
      gameManager.flipCard(i, Color.RED);
    }
    assertEquals(Color.RED, gameManager.getWinner());
  }

  @Test
  void testGetWinner_redStartsBlueWins() {
    GameManager gameManager = helperMethodGenerateFullCardList(Color.BLUE, Color.RED);

    for (int i = 0; i < 8; i++) {
      gameManager.flipCard(i, Color.BLUE);
    }
    assertEquals(Color.BLUE, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueStartsRedWins() {
    GameManager gameManager = helperMethodGenerateFullCardList(Color.RED, Color.BLUE);

    for (int i = 0; i < 8; i++) {
      gameManager.flipCard(i, Color.RED);
    }
    assertEquals(Color.RED, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueStartsBlueWins() {
    GameManager gameManager = helperMethodGenerateFullCardList(Color.BLUE, Color.BLUE);

    for (int i = 0; i < 9; i++) {
      gameManager.flipCard(i, Color.BLUE);
    }
    assertEquals(Color.BLUE, gameManager.getWinner());
  }

  @Test
  void testGetWinner_redFoundBlackCardFound() {
    Card card1 = new Card("Test", Color.BLACK);
    List<Card> cardList = List.of(card1);

    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.RED);
    assertEquals(Color.BLUE, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueFoundBlackCardFound() {
    Card card1 = new Card("Test", Color.BLACK);
    List<Card> cardList = List.of(card1);

    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.BLUE);
    assertEquals(Color.RED, gameManager.getWinner());
  }
}
