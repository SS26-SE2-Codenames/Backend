package com.codenames.codenames_backend.model.action;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PassActionTest {

    @Test
    void shouldCreatePassAction() {
        PassAction action = new PassAction();
        assertNotNull(action);
    }
}