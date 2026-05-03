package com.codenames.codenames.backend.websocket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/** Unit tests for {@link WebSocketConfig}. */
class WebSocketConfigTest {
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

    when(registry.addEndpoint("/ws")).thenReturn(endpointRegistration);
    when(registry.addEndpoint("/ws-fallback")).thenReturn(endpointRegistration);

    var sockJsRegistration =
        mock(org.springframework.web.socket.config.annotation.SockJsServiceRegistration.class);

    String[] origins = new String[] {"http://localhost:8080", "http://10.0.2.2:8080"};

    ReflectionTestUtils.setField(config, "allowedOrigins", origins);

    when(endpointRegistration.setAllowedOrigins(origins)).thenReturn(endpointRegistration);

    when(endpointRegistration.withSockJS()).thenReturn(sockJsRegistration);

    config.registerStompEndpoints(registry);

    verify(registry).addEndpoint("/ws");
    verify(endpointRegistration, Mockito.times(2)).setAllowedOrigins(origins);
    verify(endpointRegistration).withSockJS();
    verify(registry).addEndpoint("/ws-fallback");
  }
}
