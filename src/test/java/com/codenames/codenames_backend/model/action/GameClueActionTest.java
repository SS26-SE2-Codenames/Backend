package com.codenames.codenames_backend.model.action;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GiveClueActionTest {

    @Test
    void shouldCreateGiveClueAction() {
        GiveClueAction action = new GiveClueAction("Love", 3);

        assertEquals("Love", action.getClueWord());
        assertEquals(3, action.getNumber());
    }
}
