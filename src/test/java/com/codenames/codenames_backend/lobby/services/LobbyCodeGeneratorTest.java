package com.codenames.codenames_backend.lobby.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LobbyCodeGeneratorTest {

    private LobbyCodeGenerator generator;

    @BeforeEach
    void setup() {
        this.generator = new LobbyCodeGenerator();
    }

    @Test
    void testCodeGeneration_ReturnsStringOfCodeSize() {
        String result = this.generator.generateLobbyCode();

        Assertions.assertEquals(5, result.length());
    }
}
