package com.codenames.codenames_backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class GameWebSocketConfig implements WebSocketConfigurer {

  private final GameWebSocketHandler gameWebSocketHandler;

  public GameWebSocketConfig(GameWebSocketHandler gameWebSocketHandler) {
    this.gameWebSocketHandler = gameWebSocketHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(gameWebSocketHandler, "/ws/game").setAllowedOrigins("*");
  }
}
