package com.codenames.codenames_backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/** Configuration class for registering WebSocket handlers. */
@Configuration
@EnableWebSocket
public class GameWebSocketConfig implements WebSocketConfigurer {

  private final GameWebSocketHandler gameWebSocketHandler;

  /**
   * Creates a new WebSocket configuration.
   *
   * @param gameWebSocketHandler the handler for game WebSocket connections
   */
  public GameWebSocketConfig(GameWebSocketHandler gameWebSocketHandler) {
    this.gameWebSocketHandler = gameWebSocketHandler;
  }

  /**
   * Registers WebSocket handlers and their endpoints.
   *
   * @param registry the WebSocket handler registry
   */
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(gameWebSocketHandler, "/ws/game").setAllowedOrigins("*");
  }
}
