package com.codenames.codenames_backend.lobby.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private LobbyService service;

  @Test
  void createLobbyShouldReturn200() throws Exception {
    when(service.createLobby("TestUser")).thenReturn("ABCDE");

    mockMvc
        .perform(post("/lobby/create").param("username", "TestUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Successfully created Lobby."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));
  }

  @Test
  void createLobbyBlankLobbyCode() throws Exception {
    when(service.createLobby("TestUser")).thenReturn("");

    mockMvc
        .perform(post("/lobby/create").param("username", "TestUser"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Error while creating lobby."))
        .andExpect(jsonPath("$.lobbyCode").value(""));
  }

  @Test
  void joinLobbyShouldReturn200WhenSuccess() throws Exception {
    when(service.joinLobby("TestUser", "ABCDE")).thenReturn(true);

    mockMvc
        .perform(post("/lobby/join").param("username", "TestUser").param("lobbyCode", "ABCDE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Joined Lobby successfully."));
  }

  @Test
  void joinLobbyShouldReturn400WhenNotFound() throws Exception {
    when(service.joinLobby("TestUser", "XXXXX")).thenReturn(false);

    mockMvc
        .perform(post("/lobby/join").param("username", "TestUser").param("lobbyCode", "XXXXX"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Could not find lobby."));
  }

  @Test
  void leaveLobbyShouldReturn200WhenSuccess() throws Exception {
    when(service.leaveLobby("TestUser", "ABCDE")).thenReturn(true);

    mockMvc
        .perform(post("/lobby/leave").param("username", "TestUser").param("lobbyCode", "ABCDE"))
        .andExpect(status().isOk());
  }

  @Test
  void leaveLobbyNoSuccess() throws Exception {
    when(service.leaveLobby("TestUser", "ABCDE")).thenReturn(false);

    mockMvc
        .perform(post("/lobby/leave").param("username", "TestUser").param("lobbyCode", "ABCDE"))
        .andExpect(status().isBadRequest());
  }
}
