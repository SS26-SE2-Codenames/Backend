package com.codenames.codenames_backend.gameplay;

import com.codenames.codenames_backend.playingfield.Board;
import com.codenames.codenames_backend.playingfield.Card;
import java.util.List;

/** Handles the validation of clues from the spymaster. */
public class ClueValidationService {

  /**
   * Checks if the word passed by spymaster is a word already on the board.
   *
   * @param board the board of the game session
   * @param word the clue from the spymaster
   * @return the validation if the clue is valid or not
   */
  public boolean validateWord(Board board, String word) {
    if (word.contains(" ")) {
      return false;
    }
    List<Card> cardList = board.getCardList();
    for (Card card : cardList) {
      if (card.getWord().toLowerCase().equals(word.toLowerCase())) {
        return false;
      }
    }
    return true;
  }
}
