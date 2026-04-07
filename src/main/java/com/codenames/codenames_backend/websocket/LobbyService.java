package com.codenames.codenames_backend.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Service responsible for managing game lobbies and their players. */
@Service
public class LobbyService {

  private final Map<String, List<Player>> lobbies = new ConcurrentHashMap<>();

  /**
   * Adds a player to a lobby.
   *
   * @param code the lobby code
   * @param player the player to add
   */
  public void addPlayer(String code, Player player) {
    lobbies.putIfAbsent(code, Collections.synchronizedList(new ArrayList<>()));
    lobbies.get(code).add(player);
  }

  /**
   * Returns an immutable list of players in a lobby.
   *
   * @param code the lobby code
   * @return list of players in the lobby
   */
  public List<Player> getPlayers(String code) {
    return List.copyOf(lobbies.getOrDefault(code, new ArrayList<>()));
  }

  /**
   * Removes a player associated with the given session from all lobbies.
   *
   * @param session the WebSocket session to remove
   */
  public void removePlayer(WebSocketSession session) {
    lobbies
        .values()
        .forEach(players -> players.removeIf(p -> p.getSession().getId().equals(session.getId())));
  }
}
