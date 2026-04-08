package com.codenames.codenames_backend.playingfield;

import lombok.Getter;

/** Represents a single card on a board. */
@Getter
public class Card {
  private final String word;
  private final Color color;
  private boolean isGuessed;

  /**
   * Constructs a new card with the word and color assigned.
   *
   * @param word the word displayed on the card
   * @param color the color of the card
   */
  public Card(String word, Color color) {
    if (word == null || word.isEmpty()) {
      throw new IllegalArgumentException("word cannot be null or empty");
    }
    if (color == null) {
      throw new IllegalArgumentException("color cannot be null");
    }

    this.word = word;
    this.color = color;
    this.isGuessed = false;
  }

  /** Sets isGuessed to true. */
  public void setIsGuessedTrue() {
    this.isGuessed = true;
  }
}
