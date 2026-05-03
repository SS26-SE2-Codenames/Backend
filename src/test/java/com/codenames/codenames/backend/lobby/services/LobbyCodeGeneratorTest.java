package com.codenames.codenames.backend.lobby.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LobbyCodeGenerator}.
 *
 * <p>Ensures that generated lobby codes meet expected constraints.
 */
class LobbyCodeGeneratorTest {

  private LobbyCodeGenerator generator;

  @BeforeEach
  void setup() {
    this.generator = new LobbyCodeGenerator();
  }

  @Test
  void testCodeGenerationReturnsStringOfCodeSize() {
    String result = this.generator.generateLobbyCode();

    Assertions.assertEquals(5, result.length());
  }
}
