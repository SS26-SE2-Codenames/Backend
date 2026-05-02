package com.codenames.codenames.backend.gameplay;

import com.codenames.codenames.backend.playingfield.Board;
import com.codenames.codenames.backend.playingfield.Card;
import java.util.List;

/** Handles the validation of clues from the spymaster. */
public class ClueValidationService {

  /**
   * Validates clues from spymaster.
   *
   * @param board the board of the game session
   * @param word the clue from the spymaster
   * @return the validation if the clue is valid (true) or not (false
   * @throws IllegalArgumentException if the clue is null or empty, or contains spaces
   */
  public boolean validateWord(Board board, String word) {
    if (word == null || word.isEmpty()) {
      throw new IllegalArgumentException("Word is null or empty");
    }

    String cleanWord = word.trim();
    if (cleanWord.contains(" ")) {
      return false;
    }

    List<Card> cardList = board.getCardList();
    for (Card card : cardList) {
      if (card.getWord().equalsIgnoreCase(cleanWord)) {
        return false;
      }
    }
    return true;
  }
}
