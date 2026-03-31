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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Unit test for GameManager. */
public class GameManagerTest {
  private static final int TOTAL_CARDS = 25;
  private static final int STARTING_TEAM_CARDS = 9;
  private static final int SECOND_TEAM_CARDS = 8;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;
  private CardGenerator mockCardGenerator;

  @BeforeEach
  void setUp() {
    mockCardGenerator = mock(CardGenerator.class);
    mockCardGeneration(new ArrayList<>());
  }

  // Helper Method to create board list
  private void mockCardGeneration(List<Card> cardList) {
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
  }

  @Test
  void testConstructorRedStarts() {
    new GameManager(Color.RED, mockCardGenerator);
    verify(mockCardGenerator, times(1))
        .generateCards(
            TOTAL_CARDS, STARTING_TEAM_CARDS, SECOND_TEAM_CARDS, WHITE_CARDS, BLACK_CARDS);
  }

  @Test
  void testConstructorBlueStarts() {
    new GameManager(Color.BLUE, mockCardGenerator);
    verify(mockCardGenerator, times(1))
        .generateCards(
            TOTAL_CARDS, SECOND_TEAM_CARDS, STARTING_TEAM_CARDS, WHITE_CARDS, BLACK_CARDS);
  }

  @Test
  void testConstructorNullStarts() {
    assertThrows(IllegalArgumentException.class, () -> new GameManager(null, mockCardGenerator));
  }

  @ParameterizedTest
  @EnumSource(
      value = Color.class,
      names = {"WHITE", "BLACK"})
  void testConstructorWrongColorStarts(Color color) {
    assertThrows(IllegalArgumentException.class, () -> new GameManager(color, mockCardGenerator));
  }

  @Test
  void testGetCardList() {
    mockCardGeneration(List.of(new Card("Test", Color.RED)));
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    assertEquals(1, gameManager.getCardList().size());
  }

  @Test
  void testCheckColor() {
    mockCardGeneration(List.of(new Card("Test", Color.RED)));
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
    mockCardGeneration(cardList);

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
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.RED);
    assertEquals(Color.BLUE, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueFoundBlackCardFound() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.BLUE);
    assertEquals(Color.RED, gameManager.getWinner());
  }

  @Test
  void testUpdateScoreWhiteCard() {
    Card card1 = new Card("Test", Color.WHITE);
    Card card2 = new Card("Test", Color.BLACK);
    List<Card> cardList = List.of(card1, card2);

    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.BLUE);
    gameManager.flipCard(1, Color.BLUE);
    assertEquals(Color.RED, gameManager.getWinner());
  }

  @Test
  void testFlipCard_cardAlreadyFlipped() {
    mockCardGeneration(List.of(new Card("Test", Color.WHITE)));
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.RED);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, Color.RED));
  }

  @Test
  void testFlipCard_winnerAlreadyDetermined() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    GameManager gameManager = new GameManager(Color.RED, mockCardGenerator);
    gameManager.flipCard(0, Color.BLUE);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, Color.RED));
  }
}
