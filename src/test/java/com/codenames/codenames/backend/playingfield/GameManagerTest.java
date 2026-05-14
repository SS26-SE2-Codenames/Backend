package com.codenames.codenames.backend.playingfield;

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

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for GameManager. */
class GameManagerTest {
  private static final int TOTAL_CARDS = 25;
  private static final int STARTING_TEAM_CARDS = 9;
  private static final int SECOND_TEAM_CARDS = 8;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;
  private static final Team redTeam = Team.RED;
  private static final Team blueTeam = Team.BLUE;
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
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    when(mockClueValidationService.validateWord(any(), anyString())).thenReturn(true);
  }

  // Helper Method to create board list
  private void mockCardGeneration(List<Card> cardList) {
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
  }

  private void helperMethodSubmitClue(GameManager gameManager, int guessAmount, Team callingTeam) {
    gameManager.submitClue(new Clue("Test", guessAmount), callingTeam);
  }

  // Helper method for testing permutation of getWinner()
  private @NonNull GameManager helperMethodGenerateFullCardList(
      Color cardColor, Team startingTeam) {
    List<Card> cardList = new ArrayList<>();
    for (int i = 0; i < 25; i++) {
      cardList.add(new Card("Test" + i, cardColor));
    }
    mockCardGeneration(cardList);
    GameManager fullListGameManager =
        new GameManager(startingTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(fullListGameManager, 9, startingTeam);
    return fullListGameManager;
  }

  @Test
  void testConstructorRedStarts() {
    verify(mockCardGenerator, times(1))
        .generateCards(
            TOTAL_CARDS, STARTING_TEAM_CARDS, SECOND_TEAM_CARDS, WHITE_CARDS, BLACK_CARDS);
  }

  @Test
  void testConstructorBlueStarts() {
    new GameManager(blueTeam, mockCardGenerator, mockClueValidationService);
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

  @Test
  void testGetCardList() {
    assertEquals(1, gameManager.getCardList().size());
  }

  @Test
  void testCheckColor() {
    assertEquals(Color.RED, gameManager.checkColor(0));
  }

  @Test
  void testGetWinner_null() {
    assertNull(gameManager.getWinner());
  }

  @Test
  void testGetWinner_redStartsRedWins() {
    gameManager = helperMethodGenerateFullCardList(Color.RED, redTeam);

    for (int i = 0; i < 9; i++) {
      gameManager.flipCard(i, redTeam);
    }
    assertEquals(redTeam, gameManager.getWinner());
  }

  @Test
  void testGetWinner_redStartsBlueWins() {
    gameManager = helperMethodGenerateFullCardList(Color.BLUE, redTeam);

    for (int i = 0; i < 8; i++) {
      gameManager.flipCard(i, blueTeam);
    }
    assertEquals(blueTeam, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueStartsRedWins() {
    gameManager = helperMethodGenerateFullCardList(Color.RED, blueTeam);

    for (int i = 0; i < 8; i++) {
      gameManager.flipCard(i, redTeam);
    }
    assertEquals(redTeam, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueStartsBlueWins() {
    gameManager = helperMethodGenerateFullCardList(Color.BLUE, blueTeam);

    for (int i = 0; i < 9; i++) {
      gameManager.flipCard(i, blueTeam);
    }
    assertEquals(blueTeam, gameManager.getWinner());
  }

  @Test
  void testGetWinner_redFoundBlackCardFound() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1, blueTeam);
    gameManager.flipCard(0, redTeam);
    assertEquals(blueTeam, gameManager.getWinner());
  }

  @Test
  void testGetWinner_blueFoundBlackCardFound() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1, blueTeam);
    gameManager.flipCard(0, blueTeam);
    assertEquals(redTeam, gameManager.getWinner());
  }

  @Test
  void testFlipWhiteCard() {
    mockCardGeneration(List.of(new Card("Test", Color.WHITE)));
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1, redTeam);
    gameManager.flipCard(0, redTeam);
    assertNull(gameManager.getWinner());
  }

  @Test
  void testFlipCard_cardAlreadyFlipped() {
    helperMethodSubmitClue(gameManager, 1, redTeam);
    gameManager.flipCard(0, redTeam);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, redTeam));
  }

  @Test
  void testFlipCard_winnerAlreadyDetermined() {
    mockCardGeneration(List.of(new Card("Test", Color.BLACK)));
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 1, blueTeam);
    gameManager.flipCard(0, blueTeam);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, redTeam));
  }

  @Test
  void testGetCurrentRedFoundCards() {
    int result = gameManager.getCurrentRedFound();
    assertEquals(0, result);
  }

  @Test
  void testGetCurrentBlueFoundCards() {
    int result = gameManager.getCurrentBlueFound();
    assertEquals(0, result);
  }

  @Test
  void testSubmitClue() {
    Clue validClue = new Clue("Test", 2);
    gameManager.submitClue(validClue, redTeam);
    assertEquals(validClue, gameManager.getCurrentClue());
    assertEquals(3, gameManager.getRemainingGuesses());
  }

  @Test
  void testOutOfGuesses() {
    mockCardGeneration(List.of(new Card("Test", Color.RED), new Card("Test2", Color.RED)));
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    helperMethodSubmitClue(gameManager, 0, redTeam);
    gameManager.flipCard(0, redTeam);
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(1, redTeam));
  }

  @Test
  void testGetCurrentClueWord() {
    helperMethodSubmitClue(gameManager, 1, redTeam);
    assertEquals("Test", gameManager.getCurrentClueWord());
  }

  @Test
  void testGetRemainingGuesses() {
    helperMethodSubmitClue(gameManager, 1, redTeam);
    assertEquals(2, gameManager.getRemainingGuesses());
  }

  @Test
  void testSubmitClue_invalidClue() {
    when(mockClueValidationService.validateWord(any(), anyString())).thenReturn(false);
    Clue invalidClue = new Clue("InvalidClue", 1);
    assertThrows(
        IllegalArgumentException.class, () -> gameManager.submitClue((invalidClue), redTeam));
  }

  @Test
  void testGetCurrentClueWordNullUponInitialization() {
    assertNull(gameManager.getCurrentClueWord());
  }

  @Test
  void testCorrectStart_redTeam() {
    assertEquals(redTeam, gameManager.getCurrentTurn());
  }

  @Test
  void testCorrectStart_spymaster() {
    assertEquals(Role.SPYMASTER, gameManager.getCurrentPhase());
  }

  @Test
  void testAdvanceTurn_spymasterToOperative() {
    gameManager.advanceTurn();
    assertEquals(Role.OPERATIVE, gameManager.getCurrentPhase());
  }

  @Test
  void testAdvanceTurn_spymasterToOperative_sameTeam() {
    gameManager.advanceTurn();
    assertEquals(redTeam, gameManager.getCurrentTurn());
  }

  @Test
  void testAdvanceTurnTwice_operativeToSpymaster() {
    gameManager.advanceTurn();
    gameManager.advanceTurn();
    assertEquals(Role.SPYMASTER, gameManager.getCurrentPhase());
  }

  @Test
  void testAdvanceTurnTwice_redTeamToBlueTeam() {
    gameManager.advanceTurn();
    gameManager.advanceTurn();
    assertEquals(blueTeam, gameManager.getCurrentTurn());
  }

  @Test
  void testAdvanceTurnTwice_wipeClue() {
    gameManager.advanceTurn();
    gameManager.advanceTurn();
    assertNull(gameManager.getCurrentClue());
  }

  @Test
  void testPassTurn_correctTeam() {
    gameManager.advanceTurn();
    gameManager.passTurn(redTeam);
    assertEquals(blueTeam, gameManager.getCurrentTurn());
  }

  @Test
  void testPassTurn_correctPhase() {
    gameManager.advanceTurn();
    gameManager.passTurn(redTeam);
    assertEquals(Role.SPYMASTER, gameManager.getCurrentPhase());
  }

  @Test
  void testCheckCorrectTurn_throwsWhenWrongRole() {
    assertThrows(IllegalStateException.class, () -> gameManager.flipCard(0, redTeam));
  }

  @Test
  void testCheckCorrectTurn_throwsWhenWrongTeam() {
    Clue clue = new Clue("Test", 1);
    assertThrows(IllegalStateException.class, () -> gameManager.submitClue(clue, blueTeam));
  }

  @Test
  void testPassTurn_throwsWhenSpymaster() {
    assertThrows(IllegalStateException.class, () -> gameManager.passTurn(redTeam));
  }
}
