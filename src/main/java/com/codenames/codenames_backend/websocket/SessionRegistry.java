package com.codenames.codenames_backend.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for tracking WebSocket sessions and their associated users and lobbies.
 *
 * <p>Maintains mappings between session IDs, usernames, and lobby codes to enable efficient lookup
 * during messaging and disconnect events.
 *
 * <p>This implementation is thread-safe using {@link ConcurrentHashMap}.
 */
@Component
public class SessionRegistry {
  private final Map<String, String> sessionToLobby = new ConcurrentHashMap<>();
  private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

  public void register(String sessionId, String username, String lobbyCode) {
    sessionToLobby.put(sessionId, lobbyCode);
    sessionToUser.put(sessionId, username);
  }

  public String getLobby(String sessionId) {
    return sessionToLobby.get(sessionId);
  }

  public String getUser(String sessionId) {
    return sessionToUser.get(sessionId);
  }

  public void remove(String sessionId) {
    sessionToLobby.remove(sessionId);
    sessionToUser.remove(sessionId);
  }
}
