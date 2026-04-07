package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;

import static org.mockito.Mockito.*;

class GameWebSocketConfigTest {

  @Test
  void shouldRegisterWebSocketHandler() {
    GameWebSocketHandler handler = mock(GameWebSocketHandler.class);
    GameWebSocketConfig config = new GameWebSocketConfig(handler);

    WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
    WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);

    when(registry.addHandler(handler, "/ws/game")).thenReturn(registration);
    when(registration.setAllowedOrigins("http://localhost:8080")).thenReturn(registration);

    config.registerWebSocketHandlers(registry);

    verify(registry).addHandler(handler, "/ws/game");
    verify(registration).setAllowedOrigins("*");
  }
}
