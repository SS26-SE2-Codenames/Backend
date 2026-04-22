package com.codenames.codenames_backend.lobby.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.codenames.codenames_backend.websocket.Player;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.codenames.codenames_backend.lobby.Role;
import com.codenames.codenames_backend.lobby.Team;

class LobbyServiceTest {

    private LobbyService lobbyService;
    private LobbyCodeGenerator generator;

    @BeforeEach
    void setup() {
        generator = mock(LobbyCodeGenerator.class);
        lobbyService = new LobbyService(generator);
    }

    @Test
    void createLobby_ReturnLobbyCode() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");
        boolean result = lobbyService.joinLobby("TestUser", "ABCDE");

        assertTrue(result);

        List<Player> players = lobbyService.getPlayers("ABCDE");
        assertTrue(players.stream().anyMatch(p -> p.getUsername().equals("TestUser")));
    }

    @Test
    void createLobby_LobbyCodeIsNull() {
        when(generator.generateLobbyCode()).thenReturn(null);
        String result = lobbyService.createLobby("Host");

        assertNull(result);
    }

    @Test
    void createLobby_LobbyCodeIsBlank() {
        when(generator.generateLobbyCode()).thenReturn("");
        String result = lobbyService.createLobby("Host");

        assertNull(result);
    }

    @Test
    void joinLobby_ReturnFalse_LobbyNotExists() {
        boolean result = lobbyService.joinLobby("TestUser", "ABCDE");
        assertFalse(result);
    }

    @Test
    void leaveLobby_ReturnTrue_LobbyExists() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");

        boolean result = lobbyService.leaveLobby("Host", "ABCDE");

        assertTrue(result);

        List<Player> players = lobbyService.getPlayers("ABCDE");

        assertFalse(players.stream().anyMatch(p -> p.getUsername().equals("Host")));
    }

    @Test
    void leaveLobby_ReturnFalse_LobbyNotExists() {
        boolean result = lobbyService.leaveLobby("Host", "ABCDE");
        assertFalse(result);
    }

    @Test
    void createLobby_shouldGenerateNewCode_ifDuplicateExists() {
        when(generator.generateLobbyCode())
                .thenReturn("ABCDE") // erster Versuch
                .thenReturn("ABCDE") // zweiter Versuch
                .thenReturn("FGHIJ"); // dritter Versuch - anderes Ergebnis

        lobbyService.createLobby("Host1");
        String code2 = lobbyService.createLobby("Host2");

        assertEquals("FGHIJ", code2);
    }

    @Test
    void selectPosition_shouldReturnTrue_WhenPlayerChoosesTeamAndRole() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");

        boolean result = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);

        assertTrue(result);
    }

    @Test
    void selectPosition_shouldReturnFalse_WhenLobbyDoesNotExist() {
        boolean result = lobbyService.selectPosition("Host", "XXXXX", Team.RED, Role.SPYMASTER);

        assertFalse(result);
    }

    @Test
    void selectPosition_shouldReturnFalse_WhenPlayerIsNotInLobby() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");

        boolean result = lobbyService.selectPosition("Ghost", "ABCDE", Team.RED, Role.SPYMASTER);

        assertFalse(result);
    }

    @Test
    void selectPosition_shouldReturnFalse_WhenSecondSpymasterChoosesSameTeam() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");
        lobbyService.joinLobby("P1", "ABCDE");

        boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);
        boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.RED, Role.SPYMASTER);

        assertTrue(firstResult);
        assertFalse(secondResult);
    }

    @Test
    void selectPosition_shouldReturnTrue_WhenSpymastersChooseDifferentTeams() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");
        lobbyService.joinLobby("P1", "ABCDE");

        boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.SPYMASTER);
        boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.BLUE, Role.SPYMASTER);

        assertTrue(firstResult);
        assertTrue(secondResult);
    }

    @Test
    void selectPosition_shouldReturnTrue_WhenMultipleOperativesChooseSameTeam() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");
        lobbyService.createLobby("Host");
        lobbyService.joinLobby("P1", "ABCDE");

        boolean firstResult = lobbyService.selectPosition("Host", "ABCDE", Team.RED, Role.OPERATIVE);
        boolean secondResult = lobbyService.selectPosition("P1", "ABCDE", Team.RED, Role.OPERATIVE);

        assertTrue(firstResult);
        assertTrue(secondResult);
    }

    @Test
    void getPlayersShouldReturnEmptyListWhenLobbyDoesNotExist() {
        List<Player> players = lobbyService.getPlayers("UNKNOWN");

        assertNotNull(players);
        assertTrue(players.isEmpty());
    }

    @Test
    void joinLobbyShouldReturnFalseWhenPlayerAlreadyExists() {
        when(generator.generateLobbyCode()).thenReturn("ABCDE");

        lobbyService.createLobby("Host");

        boolean first = lobbyService.joinLobby("Max", "ABCDE");
        boolean second = lobbyService.joinLobby("Max", "ABCDE");

        assertTrue(first);
        assertFalse(second);

        List<Player> players = lobbyService.getPlayers("ABCDE");

        long count = players.stream()
                .filter(p -> p.getUsername().equals("Max"))
                .count();

        assertEquals(1, count);
    }
}