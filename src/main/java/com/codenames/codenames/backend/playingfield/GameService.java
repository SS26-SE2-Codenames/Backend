package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;

public class GameService {

  public void submitClue(GameManager gm, Clue clue) {
    gm.submitClue(clue);
    gm.advanceTurn();
  }

  public void flipCard(GameManager gm, int position) {
    gm.flipCard(position);
    if (gm.getRemainingGuesses() == 0) {
      gm.advanceTurn();
    }
  }

  public void endTurn(GameManager gm) {
    gm.advanceTurn();
  }

}
