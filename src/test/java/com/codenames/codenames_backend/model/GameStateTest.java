package com.codenames.codenames_backend.model;

import com.codenames.codenames_backend.model.enums.GamePhase;
import com.codenames.codenames_backend.model.enums.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {
    private GameState gameState;
    private Player redSpymaster;
    private Player blueSpymaster;

    @BeforeEach
    void setUp() {
        redSpymaster = new Player("1", "Sophia", Team.RED, true);
        blueSpymaster = new Player("2", "Anna", Team.BLUE, true);
        gameState = new GameState(Team.RED, redSpymaster, blueSpymaster);
    }

    @Test
    void shouldStartWithCorrectTeamAndPhase() {
        assertEquals(Team.RED, gameState.getCurrentTeam());
        assertEquals(GamePhase.SPYMASTER_CLUE, gameState.getCurrentPhase());
        assertEquals(redSpymaster, gameState.getCurrentSpymaster());
    }

    @Test
    void shouldSwitchTeamCorrectly() {
        gameState.switchTeam(redSpymaster, blueSpymaster);
        assertEquals(Team.BLUE, gameState.getCurrentTeam());
        assertEquals(GamePhase.SPYMASTER_CLUE, gameState.getCurrentPhase());
    }
}
