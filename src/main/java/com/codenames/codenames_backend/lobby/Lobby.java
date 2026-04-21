package com.codenames.codenames_backend.lobby;

import com.codenames.codenames_backend.websocket.Player;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

@Getter
public class Lobby {
  private static final int MAX_PLAYERS = 4;
  private final String lobbyCode;
  private final List<Player> playerList = new CopyOnWriteArrayList<>();

  public Lobby(String lobbyCode, String username) {
    this.lobbyCode = lobbyCode;
    this.addPlayer(username);
  }

  public boolean addPlayer(String username) {
    boolean alreadyExists = playerList.stream().anyMatch(p -> p.getUsername().equals(username));

    if (alreadyExists || playerList.size() >= MAX_PLAYERS) {
      return false;
    }

    playerList.add(new Player(username));
    return true;
  }

  public void removePlayer(String username) {
    playerList.removeIf(p -> p.getUsername().equals(username));
  }
}
