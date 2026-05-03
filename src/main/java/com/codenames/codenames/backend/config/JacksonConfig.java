package com.codenames.codenames.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for Jackson bean. (used for serialization into JSON) */
@Configuration
public class JacksonConfig {

  /**
   * Spring will auto-inject bean into any instance using ObjectMapper. Without this mvn will create
   * error in surefire reports when running tests.
   *
   * @return the ObjectMapper bean
   */
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
