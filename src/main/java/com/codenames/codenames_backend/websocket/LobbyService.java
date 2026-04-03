package com.codenames.codenames_backend.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyService {

  private final Map<String, List<Player>> lobbies = new HashMap<>();

  public void addPlayer(String code, Player player) {
    lobbies.putIfAbsent(code, new ArrayList<>());
    lobbies.get(code).add(player);
  }

  public List<Player> getPlayers(String code) {
    return lobbies.getOrDefault(code, new ArrayList<>());
  }

  public void removePlayer(WebSocketSession session) {
    lobbies
        .values()
        .forEach(players -> players.removeIf(p -> p.getSession().equals(session.getId())));
  }
}
