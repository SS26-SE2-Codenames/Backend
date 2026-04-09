package com.codenames.codenames_backend.gameplay;

/** Clue class that holds the clue word and guess amount + 1. */
public record Clue(String word, int guessAmount) {

  /**
   * Constructs a new clue with the word and number assigned.
   *
   * @param word the word used for clue
   * @param guessAmount the amount of guesses + 1
   */
  public Clue(String word, int guessAmount) {
    if (word == null || word.isEmpty()) {
      throw new IllegalArgumentException("Word cannot be null or empty.");
    }
    if (guessAmount < 0) {
      throw new IllegalArgumentException("Number cannot be negative.");
    }
    this.word = word;
    this.guessAmount = guessAmount + 1;
  }
}
