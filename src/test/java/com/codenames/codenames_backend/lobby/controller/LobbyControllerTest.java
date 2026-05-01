package com.codenames.codenames_backend.lobby.controller;

import com.codenames.codenames_backend.utility.Role;
import com.codenames.codenames_backend.lobby.Team;
import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService service;

    @Test
    void createLobby_shouldReturn200() throws Exception {
        when(service.createLobby("TestUser")).thenReturn("ABCDE");

        mockMvc.perform(post("/lobby/create")
                        .param("username", "TestUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully created Lobby."))
                .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));
    }

    @Test
    void createLobby_BlankLobbyCode() throws Exception {
        when(service.createLobby("TestUser")).thenReturn("");

        mockMvc.perform(post("/lobby/create")
                        .param("username", "TestUser"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error while creating lobby."))
                .andExpect(jsonPath("$.lobbyCode").value(""));
    }

    @Test
    void joinLobby_shouldReturn200_whenSuccess() throws Exception {
        when(service.joinLobby("TestUser", "ABCDE")).thenReturn(true);

        mockMvc.perform(post("/lobby/join")
                        .param("username", "TestUser")
                        .param("lobbyCode", "ABCDE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Joined Lobby successfully."));
    }

    @Test
    void joinLobby_shouldReturn400_whenNotFound() throws Exception {
        when(service.joinLobby("TestUser", "XXXXX")).thenReturn(false);

        mockMvc.perform(post("/lobby/join")
                        .param("username", "TestUser")
                        .param("lobbyCode", "XXXXX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Could not find lobby."));
    }

    @Test
    void leaveLobby_shouldReturn200_whenSuccess() throws Exception {
        when(service.leaveLobby("TestUser", "ABCDE")).thenReturn(true);

        mockMvc.perform(post("/lobby/leave")
                        .param("username", "TestUser")
                        .param("lobbyCode", "ABCDE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Left lobby successfully."));
    }

    @Test
    void leaveLobby_noSuccess() throws Exception {
        when(service.leaveLobby("TestUser", "ABCDE")).thenReturn(false);

        mockMvc.perform(post("/lobby/leave")
                        .param("username", "TestUser")
                        .param("lobbyCode", "ABCDE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Could not find lobby."));
    }

    @Test
    void selectPosition_shouldReturn200_whenSuccess() throws Exception {
        when(service.selectPosition("TestUser", "ABCDE", Team.RED, Role.SPYMASTER)).thenReturn(true);

        mockMvc.perform(post("/lobby/select-position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "TestUser",
                                  "lobbyCode": "ABCDE",
                                  "team": "RED",
                                  "role": "SPYMASTER"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Position selected successfully."))
                .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));
    }

    @Test
    void selectPosition_shouldReturn400_whenAssignmentFails() throws Exception {
        when(service.selectPosition("TestUser", "ABCDE", Team.RED, Role.SPYMASTER)).thenReturn(false);

        mockMvc.perform(post("/lobby/select-position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "TestUser",
                                  "lobbyCode": "ABCDE",
                                  "team": "RED",
                                  "role": "SPYMASTER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Could not assign selected team/role."))
                .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));
    }
}