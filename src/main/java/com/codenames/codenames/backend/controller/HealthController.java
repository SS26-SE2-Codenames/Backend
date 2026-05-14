package com.codenames.codenames.backend.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing a simple health check endpoint to verify that the backend application is
 * running.
 */
@RestController
public class HealthController {

  /**
   * Returns the current health status of the backend.
   *
   * @return a map containing the application status
   */
  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "UP");
  }
}
