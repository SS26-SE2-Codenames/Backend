package com.codenames.codenames_backend;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void shouldNotAssignTeamsAndRolesWhenLessThanFourPlayers() {
        Game game = new Game();

        game.addPlayer(new Player("A"));
        game.addPlayer(new Player("B"));
        game.addPlayer(new Player("C"));

        game.assignTeamsAndRoles();

        for (Player player : game.getPlayers()) {
            assertNull(player.getTeam());
            assertNull(player.getRole());
        }
    }

    @Test
    public void shouldAssignTeamToEveryPlayerWhenFourPlayersExist() {
        Game game = new Game();

        game.addPlayer(new Player("A"));
        game.addPlayer(new Player("B"));
        game.addPlayer(new Player("C"));
        game.addPlayer(new Player("D"));

        game.assignTeamsAndRoles();

        for (Player player : game.getPlayers()) {
            assertNotNull(player.getTeam());
            assertNotNull(player.getRole());
        }
    }

    @Test
    public void shouldHaveExactlyOneSpymasterPerTeam() {
        Game game = new Game();

        game.addPlayer(new Player("A"));
        game.addPlayer(new Player("B"));
        game.addPlayer(new Player("C"));
        game.addPlayer(new Player("D"));
        game.addPlayer(new Player("E"));

        game.assignTeamsAndRoles();

        int redSpymasterCount = 0;
        int blueSpymasterCount = 0;

        for (Player player : game.getPlayers()) {
            if (player.getTeam() == Team.RED && player.getRole() == Role.SPYMASTER) {
                redSpymasterCount++;
            }
            if (player.getTeam() == Team.BLUE && player.getRole() == Role.SPYMASTER) {
                blueSpymasterCount++;
            }
        }

        assertEquals(1, redSpymasterCount);
        assertEquals(1, blueSpymasterCount);
    }

    @Test
    public void shouldAssignAllOtherPlayersAsOperatives() {
        Game game = new Game();

        game.addPlayer(new Player("A"));
        game.addPlayer(new Player("B"));
        game.addPlayer(new Player("C"));
        game.addPlayer(new Player("D"));
        game.addPlayer(new Player("E"));

        game.assignTeamsAndRoles();

        int operativeCount = 0;

        for (Player player : game.getPlayers()) {
            if (player.getRole() == Role.OPERATIVE) {
                operativeCount++;
            }
        }

        assertEquals(game.getPlayers().size() - 2, operativeCount);
    }

    @Test
    public void shouldAllowUnevenTeams() {
        Game game = new Game();

        game.addPlayer(new Player("A"));
        game.addPlayer(new Player("B"));
        game.addPlayer(new Player("C"));
        game.addPlayer(new Player("D"));
        game.addPlayer(new Player("E"));

        game.assignTeamsAndRoles();

        int redCount = 0;
        int blueCount = 0;

        for (Player player : game.getPlayers()) {
            if (player.getTeam() == Team.RED) {
                redCount++;
            } else if (player.getTeam() == Team.BLUE) {
                blueCount++;
            }
        }

        assertEquals(3, redCount);
        assertEquals(2, blueCount);
    }

    @Test
    public void shouldDecideAValidStartingTeam() {
        Game game = new Game();

        game.decideStartingTeam();

        assertNotNull(game.getStartingTeam());
        assertTrue(game.getStartingTeam() == Team.RED || game.getStartingTeam() == Team.BLUE);
    }

    @Test
    public void shouldStartApplication() {
        assertDoesNotThrow(() -> {
            CodenamesBackendApplication.main(new String[]{});
        });
    }
}