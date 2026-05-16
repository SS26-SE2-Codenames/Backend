package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;
import lombok.Getter;

/** DTO representing the current game state. */
@Getter
public class GameStateDto {
  private final List<Card> cards;
  private final Clue currentClue;
  private final int remainingGuesses;
  private final Team winner;
  private final Team currentTurn;
  private final Role currentPhase;

  /**
   * Creates a new game state DTO.
   *
   * @param cards current board cards
   * @param currentClue current clue
   * @param remainingGuesses remaining guesses
   * @param winner winning team or null
   */
  public GameStateDto(
      List<Card> cards,
      Clue currentClue,
      int remainingGuesses,
      Team winner,
      Team currentTurn,
      Role currentPhase) {

    this.cards = cards;
    this.currentClue = currentClue;
    this.remainingGuesses = remainingGuesses;
    this.winner = winner;
    this.currentTurn = currentTurn;
    this.currentPhase = currentPhase;
  }
}
