package com.codenames.codenames_backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames_backend.gameplay.Clue;
import com.codenames.codenames_backend.gameplay.ClueValidationService;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Unit test for GameManager. */
class GameManagerTest {
  private static final int TOTAL_CARDS = 25;
  private static final int STARTING_TEAM_CARDS = 9;
  private static final int SECOND_TEAM_CARDS = 8;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;
  private GameManager gameManager;
  private CardGenerator mockCardGenerator;
  private ClueValidationService mockClueValidationService;

  // Default gameManager will have a board with 1 [Test, RED, unguessed]
  // Red team starts and all clues are valid by default, can be overridden in individual tests
  @BeforeEach
  void setUp() {
    mockCardGenerator = mock(CardGenerator.class);
    mockClueValidationService = mock(ClueValidationService.class);
    mockCardGeneration(List.of(new Card("Test", Color.RED)));
    gameManager = new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    when(mockClueValidationService.validateWord(any(), anyString())).thenReturn(true);

  }

  // Helper Method to create board list
  private void mockCardGeneration(List<Card> cardList) {
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
  }

  @Test
  void testConstructorRedStarts() {
    verify(mockCardGenerator, times(1))
        .generateCards(
            TOTAL_CARDS, STARTING_TEAM_CARDS, SECOND_TEAM_CARDS, WHITE_CARDS, BLACK_CARDS);
  }

  @Test
  void testConstructorBlueStarts() {
    new GameManager(Color.BLUE, mockCardGenerator, mockClueValidationService);
    verify(mockCardGenerator, times(1))
        .generateCards(
            TOTAL_CARDS, SECOND_TEAM_CARDS, STARTING_TEAM_CARDS, WHITE_CARDS, BLACK_CARDS);
  }

  @Test
  void testConstructorNullStarts() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new GameManager(null, mockCardGenerator, mockClueValidationService));
  }

  @ParameterizedTest
  @EnumSource(
      value = Color.class,
      names = {"WHITE", "BLACK"})
  void testConstructorWrongColorStarts(Color color) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new GameManager(color, mockCardGenerator, mockClueValidationService));
  }

  @Test
  void testGetCardList() {
    assertEquals(1, gameManager.getCardList().size());
  }

  @Test
  void testCheckColor() {
    GameManager gameManager =
        new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    assertEquals(Color.RED, gameManager.checkColor(0));
  }

  @Test
  void testGetWinner_null() {
    assertNull(gameManager.getWinner());
  }

  private void helperMethodSubmitClue(GameManager gameManager, int guessAmount) {
    gameManager.submitClue(new Clue("Test", guessAmount));
  }

  // Helper method for testing permutation of getWinner()
  private @NonNull GameManager helperMethodGenerateFullCardList(
      Color cardColor, Color startingTeam) {
    List<Card> cardList = new ArrayList<>();
    for (int i = 0; i < 25; i++) {
      cardList.add(new Card("Test" + i, cardColor));
    }
    mockCardGeneration(cardList);
    GameManager gameManager =
        new GameManager(startingTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 9);
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
    gameManager = new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1);
    gameManager.flipCard(0, Color.RED);
    assertEquals(Color.BLUE, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueFoundBlackCardFound() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    gameManager = new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1);
    gameManager.flipCard(0, Color.BLUE);
    assertEquals(Color.RED, gameManager.getWinner());
  }

  @Test
  void testFlipWhiteCard() {
    mockCardGeneration(List.of(new Card("Test", Color.WHITE)));
    gameManager = new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1);
    gameManager.flipCard(0, Color.BLUE);
    assertNull(gameManager.getWinner());
  }

  @Test
  void testFlipCard_cardAlreadyFlipped() {
    helperMethodSubmitClue(gameManager, 1);
    gameManager.flipCard(0, Color.RED);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, Color.RED));
  }

  @Test
  void testFlipCard_winnerAlreadyDetermined() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    gameManager = new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1);
    gameManager.flipCard(0, Color.BLUE);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, Color.RED));
  }

  @Test
  void testGetCurrentRedFoundCards() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    int result = gameManager.getCurrentRedFound();
    assertEquals(0, result);
  }

  @Test
  void testGetCurrentBlueFoundCards() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    int result = gameManager.getCurrentBlueFound();
    assertEquals(0, result);
  }

  @Test
  void testSubmitClue() {
    Clue clue = new Clue("Test", 2);
    assertEquals("Test", clue.getWord());
    assertEquals(3, clue.getGuessAmount());
  }

  @Test
  void testOutOfGuesses() {
    mockCardGeneration(List.of(new Card("Test", Color.RED), new Card("Test2", Color.RED)));
    gameManager = new GameManager(Color.RED, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 0);
    gameManager.flipCard(0, Color.RED);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(1, Color.RED));
  }

  @Test
  void testGetCurrentClueWord() {
    gameManager.submitClue((new Clue("Test", 1)));
    assertEquals("Test", gameManager.getCurrentClueWord());
  }

  @Test
  void testGetRemainingGuesses() {
    gameManager.submitClue((new Clue("Test", 1)));
    assertEquals(2, gameManager.getRemainingGuesses());
  }

  @Test
  void testSubmitClue_invalidClue() {
    when(mockClueValidationService.validateWord(any(), anyString())).thenReturn(false);
    assertThrows(IllegalArgumentException.class, () -> gameManager.submitClue((new Clue("Test", 0))));
  }
}
