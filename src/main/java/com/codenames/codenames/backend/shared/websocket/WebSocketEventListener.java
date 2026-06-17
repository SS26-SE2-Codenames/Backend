package com.codenames.codenames.backend.shared.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listener for WebSocket lifecycle events.
 *
 * <p>Handles client disconnections by cleaning up session mappings.
 */
@Component
public class WebSocketEventListener {
  private final SessionRegistry sessionRegistry;

  /**
   * Creates a new {@code WebSocketEventListener}.
   *
   * @param sessionRegistry the registry managing WebSocket sessions
   */
  public WebSocketEventListener(SessionRegistry sessionRegistry) {
    this.sessionRegistry = sessionRegistry;
  }

  /**
   * Handles a WebSocket disconnect event.
   *
   * <p>Removes transient WebSocket session mappings while preserving lobby membership for
   * reconnecting.
   *
   * @param event the disconnect event containing session information
   */
  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {

    String sessionId = event.getSessionId();

    if (sessionRegistry.getUser(sessionId) == null || sessionRegistry.getLobby(sessionId) == null) {
      return;
    }

    sessionRegistry.remove(sessionId);
  }
}
