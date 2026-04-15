package com.codenames.codenames_backend.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.mockito.Mockito.*;

public class WebSocketConfigTest {
  @Test
  void shouldConfigureMessageBroker() {
    WebSocketConfig config = new WebSocketConfig();

    MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

    config.configureMessageBroker(registry);

    verify(registry).enableSimpleBroker("/topic", "/queue");
    verify(registry).setApplicationDestinationPrefixes("/app");
  }

  @Test
  void shouldRegisterStompEndpoints() {
    WebSocketConfig config = new WebSocketConfig();

    StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
    var endpointRegistration =
        mock(
            org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration
                .class);
    var sockJsRegistration =
        mock(org.springframework.web.socket.config.annotation.SockJsServiceRegistration.class);

    when(registry.addEndpoint("/ws")).thenReturn(endpointRegistration);
    when(endpointRegistration.setAllowedOrigins("http://localhost:8080"))
        .thenReturn(endpointRegistration);
    when(endpointRegistration.withSockJS()).thenReturn(sockJsRegistration);

    config.registerStompEndpoints(registry);

    verify(registry).addEndpoint("/ws");
    verify(endpointRegistration).setAllowedOrigins("http://localhost:8080");
    verify(endpointRegistration).withSockJS();
  }
}
