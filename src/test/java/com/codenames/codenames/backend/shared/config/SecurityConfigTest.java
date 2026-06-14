package com.codenames.codenames.backend.shared.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = SecurityConfigTest.EmptyController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.allowed-origins=http://localhost:8080,http://10.0.2.2:8080")
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void healthEndpointShouldBePublic() throws Exception {
    mockMvc.perform(get("/health")).andExpect(status().isNotFound());
  }

  @Test
  void lobbyEndpointShouldBePublic() throws Exception {
    mockMvc.perform(get("/lobby/test")).andExpect(status().isNotFound());
  }

  @Test
  void lobbyPostShouldNotRequireCsrfToken() throws Exception {
    mockMvc
        .perform(post("/lobby/test").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void websocketFallbackEndpointShouldBePublic() throws Exception {
    mockMvc.perform(get("/ws-fallback")).andExpect(status().isNotFound());
  }

  @Test
  void corsPreflightShouldAllowConfiguredOrigin() throws Exception {
    mockMvc
        .perform(
            options("/lobby/test")
                .header("Origin", "http://localhost:8080")
                .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"));
  }

  @Test
  void unknownEndpointShouldBeDenied() throws Exception {
    mockMvc.perform(get("/internal")).andExpect(status().isForbidden());
  }

  @RestController
  static class EmptyController {}
}
