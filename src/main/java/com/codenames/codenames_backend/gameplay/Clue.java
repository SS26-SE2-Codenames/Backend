package com.codenames.codenames_backend.gameplay;

/** Clue class that holds the clue word and guess amount + 1. */
public class Clue {
  private String word;
  private int guessAmount;

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

  /**
   * Returns the clue word.
   *
   * @return the clue word
   */
  public String getWord() {
    return word;
  }

  /**
   * Return the guess amount + 1.
   *
   * @return the guess amount + 1
   */
  public int getGuessAmount() {
    return guessAmount;
  }
}
