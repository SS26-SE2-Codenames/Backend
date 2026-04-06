package com.codenames.codenames_backend.lobby;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    @Test
    void constructor_shouldInitializeLobbyCorrectly() {
        Lobby lobby = new Lobby("ABCDE", "Host");

        assertEquals("ABCDE", lobby.getLobbyCode());
        assertEquals(1, lobby.getPlayerList().size());
        assertTrue(lobby.getPlayerList().contains("Host"));
    }

    @Test
    void addPlayer_shouldAddPlayer() {
        Lobby lobby = new Lobby("ABCDE", "Host");

        lobby.addPlayer("P1");

        assertTrue(lobby.getPlayerList().contains("P1"));
        assertEquals(2, lobby.getPlayerList().size());
    }

    @Test
    void addPlayer_shouldNotExceedMaxPlayers() {
        Lobby lobby = new Lobby("ABCDE", "Host");

        lobby.addPlayer("P1");
        lobby.addPlayer("P2");
        lobby.addPlayer("P3");
        lobby.addPlayer("P4"); // sollte ignoriert werden

        assertEquals(4, lobby.getPlayerList().size());
    }

    @Test
    void removePlayer_shouldRemovePlayer() {
        Lobby lobby = new Lobby("ABCDE", "Host");

        lobby.addPlayer("P1");
        lobby.removePlayer("P1");

        assertFalse(lobby.getPlayerList().contains("P1"));
    }

    @Test
    void removePlayer_shouldDoNothingIfPlayerNotExists() {
        Lobby lobby = new Lobby("ABCDE", "Host");

        lobby.removePlayer("Ghost");

        assertEquals(1, lobby.getPlayerList().size());
    }
}
