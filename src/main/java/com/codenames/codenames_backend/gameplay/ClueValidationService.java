package com.codenames.codenames_backend.gameplay;

import com.codenames.codenames_backend.playingfield.Board;
import com.codenames.codenames_backend.playingfield.Card;
import java.util.List;

/** Handles the validation of clues from the spymaster. */
public class ClueValidationService {

  /**
   * Validates clues from spymaster.
   *
   * @param board the board of the game session
   * @param word the clue from the spymaster
   * @return the validation if the clue is valid or not
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
