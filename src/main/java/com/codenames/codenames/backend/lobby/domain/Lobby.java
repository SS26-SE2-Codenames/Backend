package com.codenames.codenames.backend.lobby.domain;

import static java.util.UUID.randomUUID;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a game lobby containing a limited number of playerList.
 *
 * <p>Supports adding and removing playerList while enforcing constraints such as maximum player
 * count and unique usernames.
 */
@Slf4j
@Getter
public class Lobby {

  private static final int MAX_PLAYERS = 4;

  private final String lobbyCode;
  private final List<Player> playerList = new CopyOnWriteArrayList<>();
  private final SecureRandom random = new SecureRandom();

  /** Maps a UUID to the selected team. */
  private final Map<String, Team> playerTeams;

  /** Maps a UUID to the selected role. */
  private final Map<String, Role> playerRoles;

  /**
   * Creates a new lobby and adds the initial player.
   *
   * @param lobbyCode the unique code identifying the lobby
   * @param username the username of the player creating the lobby
   */
  public Lobby(String lobbyCode, String username) {
    this.lobbyCode = lobbyCode;
    this.playerTeams = new HashMap<>();
    this.playerRoles = new HashMap<>();
    this.addPlayer(username, true);
  }

  /**
   * Creates an empty lobby. Primarily used during the database restoration process.
   *
   * @param lobbyCode the unique code identifying the lobby
   */

  public Lobby(String lobbyCode) {
    this.lobbyCode = lobbyCode;
    this.playerTeams = new HashMap<>();
    this.playerRoles = new HashMap<>();
  }

  /**
   * Directly adds a pre-existing player object back into the lobby.
   *
   * @param player the player to restore
   */

  public void addRestoredPlayer(Player player) {
    playerList.add(player);
  }

  /**
   * Adds a new player to the lobby or reconnects an existing player.
   *
   * @param username the username of the player
   * @param isHost whether the player is the host of the lobby
   * @param requestedUuid the UUID provided by the client to verify a reconnect
   * @return {@link Player} if the player was added or reconnected, {@code null} otherwise
   *{@code null} if the lobby is full or if the reconnect UUID was not found.
   */

  public Player addPlayer(String username, boolean isHost, String requestedUuid) {
    if (requestedUuid != null) {
      log.info("{}: reconnect attempted with UUID {}", lobbyCode, requestedUuid);
      Optional<Player> existingPlayerOpt = playerList.stream()
                  .filter(p -> p.uuid().equals(requestedUuid))
                  .findFirst();

      if (existingPlayerOpt.isPresent()) {
        log.info("{}: player '{}' reconnected with existing UUID {}", lobbyCode, username, requestedUuid);
        return existingPlayerOpt.get();
      } else {
        log.warn("{}: reconnect failed — UUID {} not found in lobby", lobbyCode, requestedUuid);
        return null;
      }
    }

    if (playerList.size() >= MAX_PLAYERS) {
      log.warn("{}: cannot add player '{}' — lobby full ({}/{})", lobbyCode, username, playerList.size(), MAX_PLAYERS);
      return null;
    }

    String newUuid = randomUUID().toString();
    Player newPlayer = new Player(username, isHost, newUuid);
    playerList.add(newPlayer);
    log.info("{}: generated UUID {} for new player '{}' (host={}, total players={})", lobbyCode, newUuid, username, isHost, playerList.size());

    return newPlayer;
  }
  /**
   * Adds a player to lobby.
   *
   * @param username the username of the player
   * @param isHost indicates which player is the host
   * @return the player or {@code null}, if lobby is full
   */

  public Player addPlayer(String username, boolean isHost) {
    return addPlayer(username, isHost, null);
  }

  /**
   * Adds a player to the lobby if capacity allows and the UUID is unique. Calls {@link
   * #addPlayer(String, boolean)} with {@code false} as the second argument
   *
   * @param username the username of the player
   * @return {@code true} if the player was added, {@code false} otherwise
   */
  public Player addPlayer(String username, String uuid) {
    return addPlayer(username, false, uuid);
  }

  /**
   * Removes a player from the lobby.
   *
   * @param uuid the uuid of the player to remove
   */
  public void removePlayer(String uuid) {
    playerList.removeIf(p -> p.uuid().equals(uuid));
    this.playerTeams.remove(uuid);
    this.playerRoles.remove(uuid);
  }

  /**
   * Checks whether a player with the given username is in the lobby.
   *
   * @param uuid the uuid to check
   * @return {@code true} if the player exists in the lobby, {@code false} otherwise
   */
  public boolean hasPlayer(String uuid) {
    return playerList.stream().anyMatch(p -> p.uuid().equals(uuid));
  }

  /**
   * Sets the team for a player.
   *
   * @param uuid the UUID of the player
   * @param team the team to assign
   */
  public void setPlayerTeam(String uuid, Team team) {
    playerTeams.put(uuid, team);
  }

  /**
   * Sets the role for a player.
   *
   * @param uuid the UUID of the player
   * @param role the role to assign
   */
  public void setPlayerRole(String uuid, Role role) {
    playerRoles.put(uuid, role);
  }

  /**
   * Returns the team of a player.
   *
   * @param uuid the UUID of the player
   * @return the assigned team, or {@code null} if none is assigned
   */
  public Team getPlayerTeam(String uuid) {
    return playerTeams.get(uuid);
  }

  /**
   * Returns the role of a player.
   *
   * @param uuid the UUID of the player
   * @return the assigned role, or {@code null} if none is assigned
   */
  public Role getPlayerRole(String uuid) {
    return playerRoles.get(uuid);
  }

  /**
   * Randomly decides which team starts the game.
   *
   * @return the team that starts the game
   */
  public Team decideStartingTeam() {
    return random.nextBoolean() ? Team.RED : Team.BLUE;
  }
}
