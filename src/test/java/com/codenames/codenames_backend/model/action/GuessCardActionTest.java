package com.codenames.codenames_backend.model.action;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GuessCardActionTest {

    @Test
    void shouldCreateGuessCardAction() {
        GuessCardAction action = new GuessCardAction(5);
        assertEquals(5, action.getCardIndex());
    }
}