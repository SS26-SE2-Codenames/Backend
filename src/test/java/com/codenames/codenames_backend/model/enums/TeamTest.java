package com.codenames.codenames_backend.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    void shouldHaveTwoTeams() {
        assertEquals(2, Team.values().length);
        assertNotNull(Team.RED);
        assertNotNull(Team.BLUE);
    }
}