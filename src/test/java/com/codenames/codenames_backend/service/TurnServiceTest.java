package com.codenames.codenames_backend.service;

import com.codenames.codenames_backend.exception.IllegalGameActionException;
import com.codenames.codenames_backend.model.*;
import com.codenames.codenames_backend.model.action.*;
import com.codenames.codenames_backend.model.enums.GamePhase;
import com.codenames.codenames_backend.model.enums.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TurnServiceTest {

    private TurnService turnService;
    private GameState gameState;
    private Player redSpymaster;
    private Player blueSpymaster;

    @BeforeEach
    void setUp() {
        turnService = new TurnService();
        redSpymaster = new Player("1", "Sophia", Team.RED, true);
        blueSpymaster = new Player("2", "Anna", Team.BLUE, true);
        gameState = new GameState(Team.RED, redSpymaster, blueSpymaster);
    }

    @Test
    void spymasterShouldGiveClueSuccessfully() {
        GiveClueAction action = new GiveClueAction("Heart", 2);

        GameState result = turnService.processAction(gameState, redSpymaster, action, redSpymaster, blueSpymaster);

        assertEquals("Heart", result.getCurrentClue());
        assertEquals(3, result.getRemainingGuesses());
        assertEquals(GamePhase.OPERATIVES_GUESSING, result.getCurrentPhase());
    }

    @Test
    void shouldThrowExceptionWhenWrongPlayerGivesClue() {
        GiveClueAction action = new GiveClueAction("Heart", 2);

        assertThrows(IllegalGameActionException.class, () ->
                turnService.processAction(gameState, blueSpymaster, action, redSpymaster, blueSpymaster)
        );
    }
}
