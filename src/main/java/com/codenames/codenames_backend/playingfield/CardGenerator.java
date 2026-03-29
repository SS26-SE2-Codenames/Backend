package com.codenames.codenames_backend.playingfield;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class CardGenerator {
    public List<String> pickWords(int totalWords) {
        ClassPathResource resource = new ClassPathResource("CodenamesWordlist.txt");
        List<String> words = new ArrayList<>();

        try (Scanner myReader = new Scanner(resource.getInputStream())) {
            while (myReader.hasNextLine()) {
                words.add(myReader.nextLine());
            }
            Collections.shuffle(words);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file");
        }
        return words.subList(0, totalWords);
    }

    public List<Card> generateCards(int totalWords, int red, int blue, int white, int black) {
        List<Card> cardList = new ArrayList<>();
        List<String> wordList = pickWords(totalWords);
        List<Color> colorList = new ArrayList<>();
        for (int i = 0; i < red; i++) {
            colorList.add(Color.RED);
        }
        for (int i = 0; i < blue; i++) {
            colorList.add(Color.BLUE);
        }
        for (int i = 0; i < white; i++) {
            colorList.add(Color.WHITE);
        }
        for (int i = 0; i < black; i++) {
            colorList.add(Color.BLACK);
        }
        for(int i = 0; i < wordList.size(); i++){
            Card card = new Card(wordList.get(i), colorList.get(i));
            cardList.add(card);
        }
        Collections.shuffle(cardList);
        return  cardList;
    }
}
