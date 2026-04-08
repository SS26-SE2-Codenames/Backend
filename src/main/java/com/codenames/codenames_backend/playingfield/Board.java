package com.codenames.codenames_backend.playingfield;

import java.util.List;
import lombok.Getter;

/** Represents the physical playing field of the game. */
public class Board {
  @Getter
  private final List<Card> cardList;

  /**
   * Constructs a new board with a generated list of cards.
   *
   * @param cardGenerator the generator to fetch words and assign colors
   * @param totalWords the total numbers of cards for a game
   * @param red the number of red cards
   * @param blue the number of blue cards
   * @param white the number of white cards
   * @param black the number of black cards
   */
  public Board(
      CardGenerator cardGenerator, int totalWords, int red, int blue, int white, int black) {
    this.cardList = cardGenerator.generateCards(totalWords, red, blue, white, black);
  }

  /**
   * Returns the card object at the position passed.
   *
   * @param position the position of the requested card
   * @return the card requested
   * @throws IllegalArgumentException if the position is out of bounds
   */
  private Card getCard(int position) {
    if (position < 0 || position >= this.cardList.size()) {
      throw new IllegalArgumentException("Invalid position");
    }
    return this.cardList.get(position);
  }

  /**
   * Returns the color of the card at the specified position.
   *
   * @param position the index of the card
   * @return the color of the card at the specified position
   * @throws IllegalArgumentException if the position is out of bounds
   */
  public Color checkColor(int position) {
    return getCard(position).getColor();
  }

  /**
   * Returns the current guessed state of a card.
   *
   * @param position the position of the card in the game board
   * @return the state of the card
   * @throws IllegalArgumentException if the position is out of bounds
   */
  public boolean getIsGuessed(int position) {
    return getCard(position).isGuessed();
  }

  /**
   * Sets the guessed state of a card to true.
   *
   * @param position the position of the card in the game board
   * @throws IllegalArgumentException if the position is out of bounds
   */
  public void setGuessed(int position) {
    getCard(position).setIsGuessedTrue();
  }
}
