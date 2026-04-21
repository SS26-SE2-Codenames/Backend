package com.codenames.codenames_backend.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for WebSocket communication using STOMP.
 *
 * <p>Enables a simple message broker and defines endpoints for client connections.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${app.allowed-origins}")
  private String[] allowedOrigins;

  /**
   * Configures the message broker used for routing messages between clients.
   *
   * <p>Enables a simple in-memory broker with destination prefixes for topics and queues, and sets
   * the application destination prefix for incoming messages.
   *
   * @param config the message broker registry to configure
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
  }

  /**
   * Registers STOMP endpoints for WebSocket communication.
   *
   * <p>Defines the endpoint clients use to connect and enables SockJS fallback options.
   *
   * @param registry the STOMP endpoint registry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOrigins(allowedOrigins).withSockJS();
  }
}
