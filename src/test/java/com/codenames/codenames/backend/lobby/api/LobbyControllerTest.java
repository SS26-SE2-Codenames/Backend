package com.codenames.codenames.backend.lobby.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import com.codenames.codenames.backend.recovery.application.SystemStatePersistenceService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private LobbyService service;

  @MockBean private SystemStatePersistenceService persistenceService;

  @Test
  void createLobbyShouldReturn200() throws Exception {
    when(service.createLobby("TestUser")).thenReturn("ABCDE");

    mockMvc
        .perform(get("/lobby/create").param("username", "TestUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Successfully created Lobby."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));

    verifyNoInteractions(persistenceService);
  }

  @Test
  void createLobbyBlankLobbyCode() throws Exception {
    when(service.createLobby("TestUser")).thenReturn("");

    mockMvc
        .perform(get("/lobby/create").param("username", "TestUser"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Error while creating lobby."))
        .andExpect(jsonPath("$.lobbyCode").value(""));
  }

  @Test
  void createLobbyNullLobbyCode() throws Exception {
    when(service.createLobby("TestUser")).thenReturn(null);

    mockMvc
        .perform(get("/lobby/create").param("username", "TestUser"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Error while creating lobby."))
        .andExpect(jsonPath("$.lobbyCode").value(""));
  }

  @Test
  void joinLobbyShouldReturn200WhenSuccess() throws Exception {
    when(service.joinLobby("TestUser", "ABCDE")).thenReturn(true);

    mockMvc
        .perform(get("/lobby/ABCDE/join").param("username", "TestUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Joined Lobby successfully."));

    verifyNoInteractions(persistenceService);
  }

  @Test
  void getLobbyInfoShouldReturn200() throws Exception {
    when(service.getPlayersDto("ABCDE"))
        .thenReturn(List.of(new PlayerDto("test", null, null, true)));
    String url = "/lobby/ABCDE";
    mockMvc.perform(get(url)).andExpect(status().isOk());
  }

  @Test
  void joinLobbyShouldReturn400WhenNotFound() throws Exception {
    when(service.joinLobby("TestUser", "XXXXX")).thenReturn(false);

    mockMvc
        .perform(get("/lobby/XXXXX/join").param("username", "TestUser"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Could not find lobby."));
  }

  @Test
  void leaveLobbyShouldReturn200WhenSuccess() throws Exception {
    when(service.getIsStarted("ABCDE")).thenReturn(false);
    when(service.leaveLobby("TestUser", "ABCDE")).thenReturn(true);

    mockMvc
        .perform(get("/lobby/ABCDE/leave").param("username", "TestUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Left lobby successfully."));

    verifyNoInteractions(persistenceService);
  }

  @Test
  void leaveLobbyNoSuccess() throws Exception {
    when(service.leaveLobby("TestUser", "ABCDE")).thenReturn(false);

    mockMvc
        .perform(
            get("/lobby/ABCDE/leave").param("username", "TestUser").param("lobbyCode", "ABCDE"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Could not find lobby."));
  }

  @Test
  void selectPositionShouldReturn200whenSuccess() throws Exception {
    when(service.selectPosition("TestUser", "ABCDE", Team.RED, Role.SPYMASTER)).thenReturn(true);

    mockMvc
        .perform(
            post("/lobby/ABCDE/select-position")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                    {
                                      "username": "TestUser",
                                      "team": "RED",
                                      "role": "SPYMASTER",
                                      "isHost": "true"
                                    }
                                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Position selected successfully."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));

    verifyNoInteractions(persistenceService);
  }

  @Test
  void selectPositionShouldReturn400WhenAssignmentFails() throws Exception {
    when(service.selectPosition("TestUser", "ABCDE", Team.RED, Role.SPYMASTER)).thenReturn(false);

    mockMvc
        .perform(
            post("/lobby/ABCDE/select-position")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                    {
                                      "username": "TestUser",
                                      "team": "RED",
                                      "role": "SPYMASTER",
                                      "isHost": "true"
                                    }
                                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Could not assign selected team/role."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"));
  }

  @Test
  void getLobbyInfoShouldReturn200WhenLobbyExists() throws Exception {
    List<PlayerDto> players =
        List.of(new PlayerDto("Alice", null, null, true), new PlayerDto("Bob", null, null, false));

    when(service.getPlayersDto("ABCDE")).thenReturn(players);

    mockMvc
        .perform(get("/lobby/ABCDE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Lobby info retrieved successfully."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"))
        .andExpect(jsonPath("$.playerList[0].username").value("Alice"))
        .andExpect(jsonPath("$.playerList[1].username").value("Bob"));
  }

  @Test
  void testStartGameReturns200WhenConditionIsMet() throws Exception {
    List<PlayerDto> players =
        List.of(new PlayerDto("Alice", null, null, true), new PlayerDto("Bob", null, null, false));

    when(service.getPlayersDto("ABCDE")).thenReturn(players);
    when(service.startGame("ABCDE", "Alice")).thenReturn(true);

    mockMvc
        .perform(get("/lobby/ABCDE/start-game").param("username", "Alice"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Game is starting now."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"))
        .andExpect(jsonPath("$.playerList[0].username").value("Alice"))
        .andExpect(jsonPath("$.isStarted").value("true"));

    verify(persistenceService).persistCurrentState();
  }

  @Test
  void testStartGameReturns400WhenServiceReturnsFalse() throws Exception {
    List<PlayerDto> players =
        List.of(new PlayerDto("Alice", null, null, true), new PlayerDto("Bob", null, null, false));

    when(service.getPlayersDto("ABCDE")).thenReturn(players);
    when(service.startGame("ABCDE", "Alice")).thenReturn(false);

    mockMvc
        .perform(get("/lobby/ABCDE/start-game").param("username", "Alice"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Could not start the game."))
        .andExpect(jsonPath("$.lobbyCode").value("ABCDE"))
        .andExpect(jsonPath("$.playerList[0].username").value("Alice"))
        .andExpect(jsonPath("$.isStarted").value("false"));
  }
}
