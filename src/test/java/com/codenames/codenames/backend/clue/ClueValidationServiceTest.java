package com.codenames.codenames.backend.clue;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.playingfield.Board;
import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import com.codenames.codenames.backend.utility.Color;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Unit tests for ClueValidationService. */
class ClueValidationServiceTest {
  private static final int TOTAL_CARDS = 25;
  private static final int STARTING_TEAM_CARDS = 9;
  private static final int SECOND_TEAM_CARDS = 8;
  private static final int WHITE_CARDS = 7;
  private static final int BLACK_CARDS = 1;

  ClueValidationService clueValidationService;
  private CardGenerator mockCardGenerator;
  private Board board;

  @BeforeEach
  void setUp() {
    clueValidationService = new ClueValidationService();
    mockCardGenerator = mock(CardGenerator.class);
    mockCardGeneration(List.of(new Card("Test", Color.RED)));
    board =
        new Board(
            mockCardGenerator,
            TOTAL_CARDS,
            STARTING_TEAM_CARDS,
            SECOND_TEAM_CARDS,
            WHITE_CARDS,
            BLACK_CARDS);
  }

  // Helper Method to create board list
  private void mockCardGeneration(List<Card> cardList) {
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Test", "Test test2", "Test "})
  void testValidateClue_clueInBoard(String input) {
    boolean value = clueValidationService.validateWord(board, input);
    assertFalse(value);
  }

  @Test
  void testValidateClue_clueNotInBoard() {
    boolean value = clueValidationService.validateWord(board, "Test1");
    assertTrue(value);
  }

  @Test
  void testValidateClue_wordNotInBoardWithSpace() {
    boolean value = clueValidationService.validateWord(board, "Test1 ");
    assertTrue(value);
  }

  @Test
  void testValidateClue_wordIsnull() {
    assertThrows(
        IllegalArgumentException.class, () -> clueValidationService.validateWord(board, null));
  }

  @Test
  void testValidateClue_wordIsEmpty() {
    assertThrows(
        IllegalArgumentException.class, () -> clueValidationService.validateWord(board, ""));
  }
}
