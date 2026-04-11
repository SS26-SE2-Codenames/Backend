package com.codenames.codenames_backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for Card. */
class CardTest {

  @Test
  void testConstructorHappyPath() {
    Card card = new Card("Test", Color.RED);
    assertEquals("Test", card.getWord());
    assertEquals(Color.RED, card.getColor());
    assertFalse(card.isGuessed());
  }

  @Test
  void testConstructorNullWord() {
    assertThrows(IllegalArgumentException.class, () -> new Card(null, Color.BLACK));
  }

  @Test
  void testConstructorEmptyWord() {
    assertThrows(IllegalArgumentException.class, () -> new Card("", Color.BLUE));
  }

  @Test
  void testConstructorNullColor() {
    assertThrows(IllegalArgumentException.class, () -> new Card("Test", null));
  }

  @Test
  void testSetIsGuessedTrue() {
    Card card = new Card("Test", Color.RED);
    card.setIsGuessedTrue();
    assertTrue(card.isGuessed());
  }
}
