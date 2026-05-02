package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codenames.codenames.backend.utility.Color;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit test for CardGenerator. */
class CardGeneratorTest {
  private CardGenerator cardGenerator;

  @BeforeEach
  void setup() {
    cardGenerator = new CardGenerator("CodenamesWordlist.txt");
  }

  @Test
  void testGenerateCardsCorrectDistribution() {
    int red = 9;
    int blue = 8;
    int white = 7;
    int black = 1;
    int total = red + blue + white + black;

    List<Card> cards = cardGenerator.generateCards(total, red, blue, white, black);

    assertEquals(total, cards.size());

    long redAmount = cards.stream().filter(card -> card.getColor() == Color.RED).count();
    long blueAmount = cards.stream().filter(card -> card.getColor() == Color.BLUE).count();
    long whiteAmount = cards.stream().filter(card -> card.getColor() == Color.WHITE).count();
    long blackAmount = cards.stream().filter(card -> card.getColor() == Color.BLACK).count();

    assertEquals(red, redAmount);
    assertEquals(blue, blueAmount);
    assertEquals(white, whiteAmount);
    assertEquals(black, blackAmount);
  }

  @Test
  void testPickWordsCorrectAmount() {
    int amount = 25;

    List<String> words = cardGenerator.pickWords(amount);

    assertEquals(amount, words.size());
  }

  @Test
  void testPickWords_invalidTotalWords() {
    int totalWords = 0;
    assertThrows(IllegalArgumentException.class, () -> cardGenerator.pickWords(totalWords));
  }

  // This makes the sum of colors 26, which is not equal to totalWords
  @Test
  void testGenerateCards_invalidTotalWords() {
    int totalWords = 25;
    int red = 9;
    int blue = 8;
    int white = 7;
    int black = 2;

    assertThrows(
        IllegalArgumentException.class,
        () -> cardGenerator.generateCards(totalWords, red, blue, white, black));
  }

  @Test
  void testPickWords_fileNotFound() {
    String nonExistentFile = "Test.txt";
    CardGenerator cardGeneratorMissingFile = new CardGenerator(nonExistentFile);
    assertThrows(IllegalStateException.class, () -> cardGeneratorMissingFile.pickWords(5));
  }
}
