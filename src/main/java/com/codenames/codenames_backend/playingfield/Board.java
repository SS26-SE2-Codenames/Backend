package com.codenames.codenames_backend.playingfield;

import java.util.List;

/** Represents the physical playing field of the game. */
public class Board {
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
   * Returns the list of cards.
   *
   * @return the list of cards
   */
  public List<Card> getCardList() {
    return cardList;
  }

  /**
   * Returns the color of the card at the specified position.
   *
   * @param position the index of the card
   * @return the color of the card at the specified position
   * @throws IllegalArgumentException if the position is out of bounds
   */
  public Color checkColor(int position) {
    if (position < 0 || position > cardList.size() - 1) {
      throw new IllegalArgumentException("Invalid position");
    }
    return cardList.get(position).getColor();
  }
}
