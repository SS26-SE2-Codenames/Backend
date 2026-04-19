package com.codenames.codenames_backend.controller;

import com.codenames.codenames_backend.model.GameState;
import com.codenames.codenames_backend.model.Player;
import com.codenames.codenames_backend.model.enums.Team;
import com.codenames.codenames_backend.model.action.GiveClueAction;
import com.codenames.codenames_backend.model.action.GuessCardAction;
import com.codenames.codenames_backend.service.TurnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TurnService turnService;

    private GameState gameState;

    @BeforeEach
    void setUp() {
        Player redSpymaster = new Player("1", "Sophia", Team.RED, true);
        Player blueSpymaster = new Player("2", "Anna", Team.BLUE, true);
        gameState = new GameState(Team.RED, redSpymaster, blueSpymaster);
    }

    @Test
    void shouldGiveClueSuccessfully() throws Exception {
        GiveClueAction action = new GiveClueAction("Herz", 3);
        when(turnService.processAction(any(), any(), any(), any(), any())).thenReturn(gameState);

        mockMvc.perform(post("/api/game/clue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clueWord\":\"Herz\", \"number\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGuessCardSuccessfully() throws Exception {
        GuessCardAction action = new GuessCardAction(5);
        when(turnService.processAction(any(), any(), any(), any(), any())).thenReturn(gameState);

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardIndex\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPassTurnSuccessfully() throws Exception {
        when(turnService.processAction(any(), any(), any(), any(), any())).thenReturn(gameState);

        mockMvc.perform(post("/api/game/pass"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidAction() throws Exception {
        when(turnService.processAction(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid action"));

        mockMvc.perform(post("/api/game/clue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clueWord\":\"\", \"number\":0}"))
                .andExpect(status().isBadRequest());
    }
}