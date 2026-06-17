package com.codenames.codenames.backend.game.application;

import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.domain.CheatResult;
import com.codenames.codenames.backend.game.domain.Clue;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.game.domain.GameManagerFactory;
import com.codenames.codenames.backend.game.mapping.DataTransferObjectService;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service class for the game. The class stores an instance of GameManager for each lobby. This also
 * exposes the methods of the GameManager, so that the websocket controllers can have message
 * mappings to allow frontend to interact with the backend.
 */
@Slf4j
@Service
public class GameService {

  private final Map<String, GameManager> games = new ConcurrentHashMap<>();
  private final GameManagerFactory gameManagerFactory;
  private final DataTransferObjectService dtoService;

  /**
   * Constructor for a GameService object.
   *
   * @param gameManagerFactory the factory responsible for generating GameManagers
   */
  public GameService(GameManagerFactory gameManagerFactory, DataTransferObjectService dtoService) {
    this.gameManagerFactory = gameManagerFactory;
    this.dtoService = dtoService;
  }

  /**
   * We have a has map with lobbyID as the key. This method adds a GM if the key does not already
   * exist.
   *
   * @param lobbyCode the lobbyID that serves as the key
   * @param startingTeam the starting team required to initialize a GM
   */
  public void createGameManager(String lobbyCode, Team startingTeam) {
    games.computeIfAbsent(lobbyCode, key -> gameManagerFactory.create(startingTeam));
    log.info("{}: a new game manager has been created.", lobbyCode);
  }

  /**
   * This method serves as a deletion of an entry in the hashmap once a lobby is no longer needed.
   *
   * @param lobbyCode the lobbyID that serves as a key to identify which GM to delete
   */
  public void removeGame(String lobbyCode) {
    games.remove(lobbyCode);
    log.info("{}: Game has been removed from list.", lobbyCode);
  }

  /**
   * Registers a recovered {@link GameManager} for a lobby after backend restart.
   *
   * @param lobbyCode lobby identifier
   * @param gameManager recovered game manager
   */
  public void restoreGameManager(String lobbyCode, GameManager gameManager) {
    games.put(lobbyCode, gameManager);
  }

  /**
   * Helper method to retrieve a GM object from the hash map.
   *
   * @param lobbyCode the lobbyID that serves as a key to identify which GM to access
   * @return the GM specified by the lobbyID
   */
  private GameManager getGame(String lobbyCode) {
    if (games.get(lobbyCode) == null) {
      throw new IllegalStateException("GameManager does not exist for lobby: " + lobbyCode);
    }
    return games.get(lobbyCode);
  }

  /**
   * Retrieves the current GameManager for a lobby.
   *
   * @param lobbyCode lobby identifier
   * @return the active GameManager
   */
  public GameManager getGameState(String lobbyCode) {
    return getGame(lobbyCode);
  }

  /**
   * The exposed clue submission method from GM that is accessed by frontend via websockets.
   *
   * @param lobbyCode the lobbyID that serves as a key to identify which GM to access
   * @param clue the clue object that needs to be validated and added to GM
   * @param callingTeam the team who called to ensure the calling team is at turn
   */
  public void submitClue(String lobbyCode, Clue clue, Team callingTeam) {
    GameManager gm = getGame(lobbyCode);
    gm.submitClue(clue, callingTeam);
  }

  /**
   * The exposed card flipping method from GM that is accessed by frontend via websockets.
   *
   * @param lobbyCode the lobbyID that serves as a key to identify which GM to access
   * @param position the position of which card is supposed to flipped on the board
   * @param callingTeam the team who called to ensure the calling team is at turn
   */
  public void flipCard(String lobbyCode, int position, Team callingTeam) {
    GameManager gm = getGame(lobbyCode);
    gm.flipCard(position, callingTeam);
  }

  /**
   * The exposed early turn ending method from GM that is accessed by frontend via websockets.
   *
   * @param lobbyCode the lobbyID that serves as a key to identify which GM to access
   * @param callingTeam the team who called to ensure the calling team is at turn
   */
  public void passTurn(String lobbyCode, Team callingTeam) {
    GameManager gm = getGame(lobbyCode);
    gm.passTurn(callingTeam);
  }

  /**
   * Maps the current game state into a @link GameStateTransferObject.
   *
   * @param lobbyCode the unique lobby code
   * @return the mapped game state transfer object
   */
  public GameStateDto getCurrentGameState(String lobbyCode) {
    GameManager gm = getGame(lobbyCode);
    return dtoService.createGameStateDto(
        gm, gm.getCurrentTurn(), gm.getCurrentPhase());
  }

  /**
   * This method uses the private method getGame to check if a game is already started via the
   * existence of a game manager.
   *
   * @param lobbyCode the lobbyCode of the lobby
   * @return if the game manager for the lobby already exists aka the game is started
   */
  public boolean isGameStarted(String lobbyCode) {
    try {
      getGame(lobbyCode);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns all active games as serializable snapshots.
   *
   * @return map of lobby codes to game state dto snapshots
   */
  public Map<String, GameStateDto> getGameSnapshots() {
    return games.keySet().stream()
        .collect(
            java.util.stream.Collectors.toMap(lobbyCode -> lobbyCode, this::getCurrentGameState));
  }

  /**
   * Performs a cheat request for the given team.
   *
   * @param lobbyCode the lobby code of the game
   * @param positions the selected card positions
   * @param team the team requesting the cheat
   * @return the cheat result or null if the request is invalid
   */
  public CheatResult useCheat(
      String lobbyCode,
      List<Integer> positions,
      Team team) {

    return getGame(lobbyCode).useCheat(positions, team);
  }

  /**
   * Performs an expose-cheat attempt and applies the matching penalty.
   *
   * @param lobbyCode the lobby code of the game
   * @param team the team trying to expose the opponent's cheat
   * @return true if the opposing team has used their cheat, false otherwise
   */
  public boolean exposeCheatAndApplyPenalty(String lobbyCode, Team team) {
    return getGame(lobbyCode).exposeCheatAndApplyPenalty(team);
  }
}
