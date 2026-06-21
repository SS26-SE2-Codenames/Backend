package com.codenames.codenames.backend.game.application;

import com.codenames.codenames.backend.game.domain.CheatResult;
import com.codenames.codenames.backend.game.domain.ExposeCheatResult;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Player;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import org.springframework.stereotype.Service;

/** Service responsible for validating and executing cheat requests. */
@Service
public class CheatService {

  private final GameService gameService;
  private final LobbyService lobbyService;

  /**
   * Creates a new cheat service.
   *
   * @param gameService service responsible for game logic
   * @param lobbyService service responsible for lobby/player data
   */
  public CheatService(GameService gameService, LobbyService lobbyService) {
    this.gameService = gameService;
    this.lobbyService = lobbyService;
  }

  /**
   * Performs a cheat request for a player.
   *
   * @param lobbyCode the lobby code
   * @param username the requesting username
   * @param positions selected card positions
   * @return the cheat result or null if the request is invalid
   */
  public CheatResult useCheat(String lobbyCode, String username, List<Integer> positions) {
    Player player = lobbyService.getPlayer(lobbyCode, username);
    if (player == null) {
      return null;
    }

    Team team = lobbyService.getPlayerTeam(player.uuid(), lobbyCode);
    Role role = lobbyService.getPlayerRole(player.uuid(), lobbyCode);

    if (team == null || role != Role.OPERATIVE) {
      return null;
    }

    return gameService.useCheat(lobbyCode, positions, team);
  }

  /**
   * Performs an expose-cheat attempt for a player and applies the matching penalty.
   *
   * @param lobbyCode the lobby code
   * @param username the requesting username
   * @return the expose-cheat result or null if the request is invalid
   */
  public ExposeCheatResult exposeCheat(String lobbyCode, String username) {
    Player player = lobbyService.getPlayer(lobbyCode, username);
    if (player == null) {
      return null;
    }

    Team team = lobbyService.getPlayerTeam(player.uuid(), lobbyCode);
    Role role = lobbyService.getPlayerRole(player.uuid(), lobbyCode);

    if (team == null || role != Role.OPERATIVE) {
      return null;
    }

    boolean correct = gameService.exposeCheatAndApplyPenalty(lobbyCode, team);
    return new ExposeCheatResult(correct, team);
  }
}
