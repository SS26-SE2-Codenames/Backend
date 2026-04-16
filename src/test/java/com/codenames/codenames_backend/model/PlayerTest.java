package com.codenames.codenames_backend.model;

import com.codenames.codenames_backend.model.enums.Team;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    @Test
    void shouldCreatePlayerWithCorrectRole() {
        Player player = new Player("1", "Sophia", Team.RED, true);

        assertEquals("1", player.getId());
        assertEquals("Sophia", player.getName());
        assertEquals(Team.RED, player.getTeam());
        assertTrue(player.isSpymaster());
    }
}
