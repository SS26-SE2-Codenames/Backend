package com.codenames.codenames_backend.lobby.services;

import com.codenames.codenames_backend.websocket.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        List<Player> players = lobbyService.getPlayers("ABCDE");assertTrue(players.stream()
                .anyMatch(p -> p.getUsername().equals("TestUser")));
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
    void getPlayers_shouldReturnEmptyList_whenLobbyDoesNotExist() {
        List<Player> players = lobbyService.getPlayers("UNKNOWN");

        assertNotNull(players);
        assertTrue(players.isEmpty());
    }
}
