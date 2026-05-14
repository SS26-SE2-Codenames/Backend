package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.utility.Team;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  public void submitClue(GameManager gm, Clue clue, Team callingTeam) {
    gm.submitClue(clue, callingTeam);
    gm.advanceTurn();
  }

  public void flipCard(GameManager gm, int position, Team callingTeam) {
    gm.flipCard(position, callingTeam);
  }

  public void passTurn(GameManager gm, Team callingTeam){
    gm.passTurn(callingTeam);
  }
}
