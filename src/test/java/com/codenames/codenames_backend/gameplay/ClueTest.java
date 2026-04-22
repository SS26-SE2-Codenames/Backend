package com.codenames.codenames_backend.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests for Clue. */
class ClueTest {
  Clue clue;

  @Test
  void testConstructorValid_word() {
    clue = new Clue("Test", 0);
    assertEquals("Test", clue.word());
  }

  @Test
  void testConstructorValid_guessAmount() {
    clue = new Clue("Test", 0);
    // Expecting 1 since operators get N + 1 guesses.
    assertEquals(1, clue.guessAmount());
  }

  @Test
  void testConstructorInvalid_word_null() {
    assertThrows(IllegalArgumentException.class, () -> new Clue(null, 0));
  }

  @Test
  void testConstructorInvalid_word_empty() {
    assertThrows(IllegalArgumentException.class, () -> new Clue("", 0));
  }

  @Test
  void testConstructorInvalid_guessAmount_negative() {
    assertThrows(IllegalArgumentException.class, () -> new Clue("Test", -1));
  }
}
