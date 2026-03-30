package com.codenames.codenames_backend.playingfield;

/** Represents a single card on a board. */
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

  /**
   * Returns the word on the card.
   *
   * @return the word of the card
   */
  public String getWord() {
    return word;
  }

  /**
   * Returns the color of the card.
   *
   * @return the color of the card
   */
  public Color getColor() {
    return color;
  }

  /**
   * Checks if the card is already guessed.
   *
   * @return the guess state of a card.
   */
  public boolean isGuessed() {
    return isGuessed;
  }

  /** Sets isGuessed to true. */
  public void reveal() {
    this.isGuessed = true;
  }
}
