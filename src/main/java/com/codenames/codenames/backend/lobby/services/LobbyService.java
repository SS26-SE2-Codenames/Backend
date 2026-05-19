package com.codenames.codenames.backend.lobby.services;

import com.codenames.codenames.backend.chat.ChatService;
import com.codenames.codenames.backend.lobby.Lobby;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.playingfield.GameService;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import com.codenames.codenames.backend.websocket.Player;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing lobbies and player interactions.
 *
 * <p>Handles creation of lobbies, player joins/leaves, and retrieval of lobby data. Ensures
 * uniqueness of lobby codes and thread-safe access to lobby storage.
 */
@Slf4j
@Service
public class LobbyService {

  @Getter private final Map<String, Lobby> lobbyList = new ConcurrentHashMap<>();
  private final LobbyCodeGenerator generator;
  private final GameService gameService;
  private final ChatService chatService;

  /**
   * Creates a new {@code LobbyService}.
   *
   * @param generator the lobby code generator used to create unique lobby codes
   */
  public LobbyService(
      LobbyCodeGenerator generator, ChatService chatService, GameService gameService) {
    this.generator = generator;
    this.chatService = chatService;
    this.gameService = gameService;
  }

  /**
   * Creates a new lobby and adds the given user as the first player.
   *
   * @param username the username of the player creating the lobby
   * @return the generated lobby code, or {@code null} if creation fails
   */
  public String createLobby(String username) {
    String lobbyCode = generateLobbyCode();
    if (lobbyCode == null || lobbyCode.isBlank()) {
      log.error("ERROR: there was an error when generating a lobby code");
      return null;
    }

    Lobby lobby = new Lobby(lobbyCode, username);
    lobbyList.put(lobbyCode, lobby);
    log.info("{}: a lobby has been created", lobbyCode);
    return lobbyCode;
  }

  /**
   * Helper method to add the GameManager once a lobby is created.
   *
   * @param lobby the lobby object to determine the starting team
   * @param lobbyCode the ID for the lobby which the GameManager is responsible for
   */
  private void addGameManagerForLobby(Lobby lobby, String lobbyCode) {
    Team start = lobby.decideStartingTeam();
    gameService.createGameManager(lobbyCode, start);
  }

  /**
   * Adds a player to an existing lobby.
   *
   * @param username the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return {@code true} if the player successfully joined, {@code false} otherwise
   */
  public boolean joinLobby(String username, String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    if (lobby != null) {
      log.info("{}: a player has joined", lobbyCode);
      return lobby.addPlayer(username);
    }
    log.error("{}: an error occurred when joining lobby", lobbyCode);
    return false;
  }

  /**
   * Registers a recovered lobby into in-memory lobby storage.
   *
   * @param lobbyCode lobby identifier
   * @param lobby recovered lobby instance
   */
  public void restoreLobby(String lobbyCode, Lobby lobby) {
    lobbyList.put(lobbyCode, lobby);
  }

  /**
   * Removes a player from a lobby.
   *
   * @param username the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return {@code true} if the player was removed, {@code false} if the lobby does not exist
   */
  public boolean leaveLobby(String username, String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    if (lobby != null) {
      lobby.removePlayer(username);
      log.info("{}: a player left", lobbyCode);
      return true;
    }
    log.error("{}: an error occurred when leaving", lobbyCode);
    return false;
  }

  /**
   * Assigns a team and role to a player in a lobby.
   *
   * @param username the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @param team the selected team
   * @param role the selected role
   * @return {@code true} if the position was assigned, {@code false} otherwise
   */
  public boolean selectPosition(String username, String lobbyCode, Team team, Role role) {
    Lobby lobby = lobbyList.get(lobbyCode);

    if (lobby == null || !lobby.hasPlayer(username) || team == null || role == null) {
      log.error("{}: position selection error occurred", lobbyCode);
      return false;
    }

    if (role == Role.SPYMASTER && isSpymasterAlreadyAssigned(lobby, username, team)) {
      log.error("{}: position selection error occurred, spymaster is already assigned.", lobbyCode);
      return false;
    }

    lobby.setPlayerTeam(username, team);
    lobby.setPlayerRole(username, role);
    log.info("{}: new role was assigned to player", lobbyCode);
    return true;
  }

  /**
   * Checks if the lobby still has players after a player leaves and removes the lobby if it is
   * empty.
   *
   * @param lobbyCode the lobby code identifying the lobby
   */
  public void checkLobbyStillHasPlayers(String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    if (lobby.getPlayerList().isEmpty()) {
      lobbyList.remove(lobbyCode);
      chatService.clearLobbyHistory(lobbyCode);
      gameService.removeGame(lobbyCode);
      log.info("{}: Lobby is empty, was removed from list.", lobbyCode);
    }
  }

  /**
   * Retrieves all playerList in the specified lobby.
   *
   * @param lobbyCode the lobby code identifying the lobby
   * @return a list of players, or an empty list if the lobby does not exist
   */
  public List<Player> getPlayers(String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    return lobby != null ? lobby.getPlayerList() : List.of();
  }

  /**
   * Retrieves all playerList in the specified lobby as PlayerDto objects.
   *
   * @param lobbyCode the lobby code identifying the lobby
   * @return a list of PlayerDto objects, or an empty list if the lobby does not exist
   */
  public List<PlayerDto> getPlayersDto(String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    if (lobby != null) {
      return lobby.getPlayerList().stream()
          .map(
              player ->
                  new PlayerDto(
                      player.username(),
                      lobby.getPlayerTeam(player.username()),
                      lobby.getPlayerRole(player.username()),
                      player.isHost()))
          .toList();
    }
    return List.of();
  }

  /**
   * Checks whether a spymaster is already assigned for the given team in the lobby.
   *
   * @param lobby the lobby to inspect
   * @param username the username requesting the role
   * @param team the team to inspect
   * @return {@code true} if a different player is already the spymaster for that team
   */
  private boolean isSpymasterAlreadyAssigned(Lobby lobby, String username, Team team) {
    for (Player player : lobby.getPlayerList()) {
      if (!player.username().equals(username)
          && lobby.getPlayerTeam(player.username()) == team
          && lobby.getPlayerRole(player.username()) == Role.SPYMASTER) {
        return true;
      }
    }
    return false;
  }

  /**
   * Generates a unique lobby code.
   *
   * @return a unique lobby code, or {@code null} if no valid code could be generated
   */
  private String generateLobbyCode() {
    String code = generator.generateLobbyCode();

    if (code == null || code.isBlank()) {
      return null;
    }

    while (lobbyList.containsKey(code)) {
      code = generator.generateLobbyCode();
      if (code == null || code.isBlank()) {
        return null;
      }
    }
    return code;
  }

  /**
   * Retrieves the team of a player in a lobby.
   *
   * @param username the username of a player
   * @param lobbyCode the lobby code of the lobby
   * @return the team of the player, or {@code null} if the lobby or player does not exist
   */
  public Team getPlayerTeam(String username, String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    if (lobby != null) {
      return lobby.getPlayerTeam(username);
    }
    return null;
  }

  /**
   * Retrieves the role of a player in a lobby.
   *
   * @param username the username of a player
   * @param lobbyCode the lobby code of the lobby
   * @return the role of the player, or {@code null} if the lobby or player does not exist
   */
  public Role getPlayerRole(String username, String lobbyCode) {
    Lobby lobby = lobbyList.get(lobbyCode);
    if (lobby != null) {
      return lobby.getPlayerRole(username);
    }
    return null;
  }

  /**
   * The service method for starting a game. This creates a game manager object for the lobby and
   * checks if the requesting user is liable to start the game.
   *
   * @param lobbyCode the unique lobby code
   * @param username the name of the requesting user
   * @return if starting was successful
   */
  public boolean startGame(String lobbyCode, String username) {
    boolean isStarted =
        !lobbyCode.isBlank() && !username.isBlank() && Objects.equals(getHost(lobbyCode), username);
    Lobby lobby = lobbyList.get(lobbyCode);
    addGameManagerForLobby(lobby, lobbyCode);

    log.info("{}: Game start requested, returning: {}", lobbyCode, isStarted);
    return isStarted;
  }

  /**
   * This method computes the host of a lobby.
   *
   * @param lobbyCode the unique lobby code
   * @return the username of the host
   */
  public String getHost(String lobbyCode) {
    if (lobbyCode == null || lobbyCode.isBlank()) {
      return "";
    }
    List<Player> players = getPlayers(lobbyCode);
    if (players.isEmpty()) {
      return "";
    }
    for (Player p : players) {
      if (p.isHost()) {
        return p.username();
      }
    }
    return "";
  }

  /**
   * Checks if the game is started by looking after an existing game manager object.
   *
   * @param lobbyCode the unique lobby code
   * @return whether a game manager exists (@code true or @code false)
   */
  public boolean getIsStarted(String lobbyCode) {
    return gameService.isGameStarted(lobbyCode);
  }

  /**
   * Returns all lobbies as serializable snapshots.
   *
   * @return map of lobby codes to player dto lists
   */
  public Map<String, List<PlayerDto>> getLobbySnapshots() {
    return lobbyList.keySet().stream()
        .collect(java.util.stream.Collectors.toMap(lobbyCode -> lobbyCode, this::getPlayersDto));
  }
}
