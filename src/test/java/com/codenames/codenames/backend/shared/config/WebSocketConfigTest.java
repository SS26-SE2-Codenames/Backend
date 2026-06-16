package com.codenames.codenames.backend.shared.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
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
  void shouldRegisterRawWebSocketEndpoint() {
    WebSocketConfig config = new WebSocketConfig();

    StompEndpointRegistry registry = mock(StompEndpointRegistry.class);

    var endpointRegistration =
        mock(
            org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration
                .class);

    String[] origins = new String[] {"http://localhost:8080", "http://10.0.2.2:8080"};

    ReflectionTestUtils.setField(config, "allowedOrigins", origins);

    when(registry.addEndpoint("/ws-fallback")).thenReturn(endpointRegistration);
    when(endpointRegistration.setAllowedOrigins(origins)).thenReturn(endpointRegistration);

    config.registerStompEndpoints(registry);

    verify(registry).addEndpoint("/ws-fallback");
    verify(endpointRegistration).setAllowedOrigins(origins);
  }
}
