package com.codenames.codenames_backend.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GamePhaseTest {

    @Test
    void shouldContainAllPhases() {
        assertEquals(3, GamePhase.values().length);
        assertNotNull(GamePhase.SPYMASTER_CLUE);
        assertNotNull(GamePhase.OPERATIVES_GUESSING);
        assertNotNull(GamePhase.GAME_OVER);
    }
}