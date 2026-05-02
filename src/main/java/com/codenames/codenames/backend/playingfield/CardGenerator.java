package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.utility.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/** Utility class for reading the file of words and generating random cards. */
@Component
public class CardGenerator {

  private final String wordFile;

  /** Default constructor that passes the txt file to the dependency injection constructor. */
  public CardGenerator() {
    this("CodenamesWordlist.txt");
  }

  /**
   * DI constructor implemented to ease testing.
   *
   * @param wordFile the txt file containing the words used for the cards
   */
  public CardGenerator(String wordFile) {
    this.wordFile = wordFile;
  }

  /**
   * Selects a random amount of words from the file.
   *
   * @param totalWords the amount of words to pick
   * @return a random list of words
   * @throws IllegalArgumentException if totalWords is less than or equal 0
   * @throws IllegalStateException if there is an error reading the file
   */
  public List<String> pickWords(int totalWords) {
    if (totalWords <= 0) {
      throw new IllegalArgumentException("totalWords must be greater than 0");
    }
    ClassPathResource resource = new ClassPathResource(this.wordFile);
    List<String> words = new ArrayList<>();

    try (Scanner myReader = new Scanner(resource.getInputStream())) {
      while (myReader.hasNextLine()) {
        words.add(myReader.nextLine());
      }
      Collections.shuffle(words);
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
    return words.subList(0, totalWords);
  }

  /**
   * Generates a random list of cards with words and colors assigned.
   *
   * @param totalWords the amount of cards to generate
   * @param red the amount of red cards
   * @param blue the amount of blue cards
   * @param white the amount of white cards
   * @param black the amount of black cards
   * @return a randomized list of card objects
   * @throws IllegalArgumentException if totalWords is less than or equal 0 or if totalWords does
   *     not match the sum of the colors
   * @throws IllegalStateException if there is an error reading the file
   */
  public List<Card> generateCards(int totalWords, int red, int blue, int white, int black) {
    if (totalWords != red + blue + white + black) {
      throw new IllegalArgumentException("totalWords must match sum of colors");
    }
    List<Color> colorList = new ArrayList<>();
    addColorToList(colorList, red, Color.RED);
    addColorToList(colorList, blue, Color.BLUE);
    addColorToList(colorList, white, Color.WHITE);
    addColorToList(colorList, black, Color.BLACK);

    List<Card> cardList = new ArrayList<>();
    List<String> wordList = pickWords(totalWords);
    for (int i = 0; i < wordList.size(); i++) {
      Card card = new Card(wordList.get(i), colorList.get(i));
      cardList.add(card);
    }
    Collections.shuffle(cardList);
    return cardList;
  }

  private void addColorToList(List<Color> colorList, int count, Color color) {
    for (int i = 0; i < count; i++) {
      colorList.add(color);
    }
  }
}
