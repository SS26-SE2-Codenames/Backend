package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.game.dto.GameStateDto;
import com.codenames.codenames.backend.utility.Team;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Service class for the game. The class stores an instance of GameManager for each lobby. This also
 * exposes the methods of the GameManager, so that the websocket controllers can have message
 * mappings to allow frontend to interact with the backend.
 */
@Service
public class GameService {

  private final Map<String, GameManager> games = new ConcurrentHashMap<>();
  private final GameManagerFactory gameManagerFactory;

  /**
   * Constructor for a GameService object.
   *
   * @param gameManagerFactory the factory responsible for generating GameManagers
   */
  public GameService(GameManagerFactory gameManagerFactory) {
    this.gameManagerFactory = gameManagerFactory;
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
  }

  /**
   * This method serves as a deletion of an entry in the hashmap once a lobby is no longer needed.
   *
   * @param lobbyCode the lobbyID that serves as a key to identify which GM to delete
   */
  public void removeGame(String lobbyCode) {
    games.remove(lobbyCode);
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
    gm.advanceTurn();
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
   * Creates a DTO representing the current game state.
   *
   * @param lobbyCode lobby identifier
   * @return DTO containing board and turn information
   */
  public GameStateDto createGameStateDto(String lobbyCode) {

    GameManager gm = getGame(lobbyCode);

    return new GameStateDto(
        gm.getCardList(),
        gm.getCurrentClue(),
        gm.getRemainingGuesses(),
        gm.getWinner(),
        gm.getCurrentTurn(),
        gm.getCurrentPhase());
  }
}
