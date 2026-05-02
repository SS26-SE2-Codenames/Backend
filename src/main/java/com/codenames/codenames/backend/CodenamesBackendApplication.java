package com.codenames.codenames.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Codenames Backend application.
 *
 * <p>Starts the Spring Boot application and initializes the context.
 */
@SpringBootApplication
public class CodenamesBackendApplication {

  /**
   * Launches the application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(CodenamesBackendApplication.class, args);
  }
}
