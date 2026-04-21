package com.codenames.codenames_backend.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Registry for managing WebSocket sessions and their associated users and lobbies.
 *
 * <p>Maintains mappings between session IDs, usernames, and lobby codes to support messaging and
 * cleanup on disconnect.
 *
 * <p>This implementation is thread-safe.
 */
@Component
public class SessionRegistry {
  private final Map<String, String> sessionToLobby = new ConcurrentHashMap<>();
  private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

  /**
   * Registers a session with its associated user and lobby.
   *
   * @param sessionId the WebSocket session ID
   * @param username the username of the player
   * @param lobbyCode the lobby code
   */
  public void register(String sessionId, String username, String lobbyCode) {
    sessionToLobby.put(sessionId, lobbyCode);
    sessionToUser.put(sessionId, username);
  }

  /**
   * Returns the lobby code associated with the given session.
   *
   * @param sessionId the WebSocket session ID
   * @return the lobby code, or {@code null} if not found
   */
  public String getLobby(String sessionId) {
    return sessionToLobby.get(sessionId);
  }

  /**
   * Returns the username associated with the given session ID.
   *
   * @param sessionId the WebSocket session ID
   * @return the username, or {@code null} if not found
   */
  public String getUser(String sessionId) {
    return sessionToUser.get(sessionId);
  }

  /**
   * Removes all mappings associated with the given session ID.
   *
   * @param sessionId the WebSocket session ID to remove
   */
  public void remove(String sessionId) {
    sessionToLobby.remove(sessionId);
    sessionToUser.remove(sessionId);
  }
}
