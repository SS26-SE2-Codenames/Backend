package com.codenames.codenames_backend.lobby;

import com.codenames.codenames_backend.utility.Team;
import com.codenames.codenames_backend.utility.Role;
import com.codenames.codenames_backend.websocket.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

/**
 * Represents a game lobby containing a limited number of players.
 *
 * <p>Supports adding and removing players while enforcing constraints such as
 * maximum player count and unique usernames.
 */
@Getter
public class Lobby {

  private static final int MAX_PLAYERS = 4;

  private final String lobbyCode;
  private final List<Player> playerList = new CopyOnWriteArrayList<>();

  /** Maps a username to the selected team. */
  private final Map<String, Team> playerTeams;

  /** Maps a username to the selected role. */
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
    this.addPlayer(username);
  }

  /**
   * Adds a player to the lobby if capacity allows and the username is unique.
   *
   * @param username the username of the player
   * @return {@code true} if the player was added, {@code false} otherwise
   */
  public boolean addPlayer(String username) {
    boolean alreadyExists = playerList.stream()
        .anyMatch(p -> p.getUsername().equals(username));

    if (alreadyExists || playerList.size() >= MAX_PLAYERS) {
      return false;
    }

    playerList.add(new Player(username));
    return true;
  }

  /**
   * Removes a player from the lobby.
   *
   * @param username the username of the player to remove
   */
  public void removePlayer(String username) {
    playerList.removeIf(p -> p.getUsername().equals(username));
    this.playerTeams.remove(username);
    this.playerRoles.remove(username);
  }

  /**
   * Checks whether a player with the given username is in the lobby.
   *
   * @param username the username to check
   * @return {@code true} if the player exists in the lobby, {@code false} otherwise
   */
  public boolean hasPlayer(String username) {
    return playerList.stream()
        .anyMatch(p -> p.getUsername().equals(username));
  }

  /**
   * Sets the team for a player.
   *
   * @param username the username of the player
   * @param team the team to assign
   */
  public void setPlayerTeam(String username, Team team) {
    playerTeams.put(username, team);
  }

  /**
   * Sets the role for a player.
   *
   * @param username the username of the player
   * @param role the role to assign
   */
  public void setPlayerRole(String username, Role role) {
    playerRoles.put(username, role);
  }

  /**
   * Returns the team of a player.
   *
   * @param username the username of the player
   * @return the assigned team, or {@code null} if none is assigned
   */
  public Team getPlayerTeam(String username) {
    return playerTeams.get(username);
  }

  /**
   * Returns the role of a player.
   *
   * @param username the username of the player
   * @return the assigned role, or {@code null} if none is assigned
   */
  public Role getPlayerRole(String username) {
    return playerRoles.get(username);
  }
}