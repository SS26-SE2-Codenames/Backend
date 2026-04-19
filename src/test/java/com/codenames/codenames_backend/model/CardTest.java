package com.codenames.codenames_backend.model;

import com.codenames.codenames_backend.model.enums.Team;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void shouldCreateCardAndRevealIt() {
        Card card = new Card("Heart", Team.RED);

        assertEquals("Heart", card.getWord());
        assertEquals(Team.RED, card.getType());
        assertFalse(card.isRevealed());

        card.reveal();
        assertTrue(card.isRevealed());
    }
}