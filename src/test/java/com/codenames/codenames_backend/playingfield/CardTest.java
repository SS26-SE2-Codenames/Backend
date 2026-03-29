package com.codenames.codenames_backend.playingfield;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

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
}
