package com.codenames.codenames_backend.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IllegalGameActionExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Only the current Spymaster can give a clue.";
        IllegalGameActionException exception = new IllegalGameActionException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(true);
    }
}