package com.codenames.codenames_backend.lobby;

import com.codenames.codenames_backend.websocket.Player;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

/**
 * Represents a game lobby containing a limited number of players.
 *
 * <p>Supports adding and removing players while enforcing constraints such as maximum player count
 * and unique usernames.
 */
@Getter
public class Lobby {
  private static final int MAX_PLAYERS = 4;
  private final String lobbyCode;
  private final List<Player> playerList = new CopyOnWriteArrayList<>();

  /**
   * Creates a new lobby and adds the initial player.
   *
   * @param lobbyCode the unique code identifying the lobby
   * @param username the username of the player creating the lobby
   */
  public Lobby(String lobbyCode, String username) {
    this.lobbyCode = lobbyCode;
    this.addPlayer(username);
  }

  /**
   * Adds a player to the lobby if capacity allows and the username is unique.
   *
   * @param username the username of the player
   * @return {@code true} if the player was added, {@code false} otherwise
   */
  public boolean addPlayer(String username) {
    boolean alreadyExists = playerList.stream().anyMatch(p -> p.getUsername().equals(username));

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
  }
}
