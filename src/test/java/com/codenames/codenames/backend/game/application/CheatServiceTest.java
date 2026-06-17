package com.codenames.codenames.backend.game.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.game.domain.CheatResult;
import com.codenames.codenames.backend.game.domain.ExposeCheatResult;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the functionality of CheatService. */
class CheatServiceTest {

  private static final String LOBBY_CODE = "ABCDE";
  private static final String USERNAME = "Max";

  private GameService gameService;
  private LobbyService lobbyService;
  private CheatService cheatService;

  @BeforeEach
  void setUp() {
    gameService = mock(GameService.class);
    lobbyService = mock(LobbyService.class);
    cheatService = new CheatService(gameService, lobbyService);
  }

  @Test
  void useCheatShouldReturnResultForOperative() {
    List<Integer> positions = List.of(0, 1);
    CheatResult expected = new CheatResult("Die Karte \"Dog\" ist richtig.", Team.RED);

    when(lobbyService.getPlayerTeam(USERNAME, LOBBY_CODE)).thenReturn(Team.RED);
    when(lobbyService.getPlayerRole(USERNAME, LOBBY_CODE)).thenReturn(Role.OPERATIVE);
    when(gameService.useCheat(LOBBY_CODE, positions, Team.RED)).thenReturn(expected);

    CheatResult result = cheatService.useCheat(LOBBY_CODE, USERNAME, positions);

    assertEquals(expected, result);
    verify(gameService).useCheat(LOBBY_CODE, positions, Team.RED);
  }

  @Test
  void useCheatShouldReturnNullWhenTeamIsMissing() {
    List<Integer> positions = List.of(0, 1);

    when(lobbyService.getPlayerTeam(USERNAME, LOBBY_CODE)).thenReturn(null);
    when(lobbyService.getPlayerRole(USERNAME, LOBBY_CODE)).thenReturn(Role.OPERATIVE);

    CheatResult result = cheatService.useCheat(LOBBY_CODE, USERNAME, positions);

    assertNull(result);
    verify(gameService, never()).useCheat(LOBBY_CODE, positions, Team.RED);
  }

  @Test
  void useCheatShouldReturnNullWhenPlayerIsNotOperative() {
    List<Integer> positions = List.of(0, 1);

    when(lobbyService.getPlayerTeam(USERNAME, LOBBY_CODE)).thenReturn(Team.RED);
    when(lobbyService.getPlayerRole(USERNAME, LOBBY_CODE)).thenReturn(Role.SPYMASTER);

    CheatResult result = cheatService.useCheat(LOBBY_CODE, USERNAME, positions);

    assertNull(result);
    verify(gameService, never()).useCheat(LOBBY_CODE, positions, Team.RED);
  }

  @Test
  void exposeCheatShouldApplyPenaltyForOperative() {
    when(lobbyService.getPlayerTeam(USERNAME, LOBBY_CODE)).thenReturn(Team.RED);
    when(lobbyService.getPlayerRole(USERNAME, LOBBY_CODE)).thenReturn(Role.OPERATIVE);
    when(gameService.exposeCheatAndApplyPenalty(LOBBY_CODE, Team.RED)).thenReturn(true);

    ExposeCheatResult result = cheatService.exposeCheat(LOBBY_CODE, USERNAME);

    assertTrue(result.correct());
    assertEquals(Team.RED, result.team());
    verify(gameService).exposeCheatAndApplyPenalty(LOBBY_CODE, Team.RED);
  }

  @Test
  void exposeCheatShouldReturnNullWhenTeamIsMissing() {
    when(lobbyService.getPlayerTeam(USERNAME, LOBBY_CODE)).thenReturn(null);
    when(lobbyService.getPlayerRole(USERNAME, LOBBY_CODE)).thenReturn(Role.OPERATIVE);

    ExposeCheatResult result = cheatService.exposeCheat(LOBBY_CODE, USERNAME);

    assertNull(result);
    verify(gameService, never()).exposeCheatAndApplyPenalty(LOBBY_CODE, Team.RED);
  }

  @Test
  void exposeCheatShouldReturnNullWhenPlayerIsNotOperative() {
    when(lobbyService.getPlayerTeam(USERNAME, LOBBY_CODE)).thenReturn(Team.RED);
    when(lobbyService.getPlayerRole(USERNAME, LOBBY_CODE)).thenReturn(Role.SPYMASTER);

    ExposeCheatResult result = cheatService.exposeCheat(LOBBY_CODE, USERNAME);

    assertNull(result);
    verify(gameService, never()).exposeCheatAndApplyPenalty(LOBBY_CODE, Team.RED);
  }
}
