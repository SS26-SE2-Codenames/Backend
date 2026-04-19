package com.codenames.codenames_backend.model;

import com.codenames.codenames_backend.model.enums.GamePhase;
import com.codenames.codenames_backend.model.enums.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameState {
    //Getter
    private Team currentTeam;
    @Setter
    private GamePhase currentPhase;
    private Player currentSpymaster;
    //Setter
    @Setter
    private String currentClue;
    @Setter
    private int remainingGuesses;
    @Setter
    private Team winner;
    private final List<Card> cards = new ArrayList<>();

    public GameState(Team startingTeam, Player redSpymaster, Player blueSpymaster) {
        this.currentTeam = startingTeam;
        this.currentPhase =GamePhase.SPYMASTER_CLUE;
        this.currentSpymaster = (startingTeam == Team.RED) ? redSpymaster : blueSpymaster;
        this.remainingGuesses = 0;
    }

    public GameState() {
    }

    public void switchTeam(Player redSpymaster, Player blueSpymaster) {
        this.currentTeam = (this.currentTeam == Team.RED) ? Team.BLUE : Team.RED;
        this.currentSpymaster = (this.currentTeam == Team.RED) ? redSpymaster :
                blueSpymaster;
        this.currentPhase = GamePhase.SPYMASTER_CLUE;
        this.currentClue = null;
        this.remainingGuesses = 0;
    }
}
