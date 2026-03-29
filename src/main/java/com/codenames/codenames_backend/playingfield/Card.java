package com.codenames.codenames_backend.playingfield;

import java.util.List;

public class Card {
    private final String word;
    private final Color color;
    private boolean isGuessed;

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

    public String getWord() {
        return word;
    }

    public Color getColor() {
        return color;
    }

    public boolean isGuessed() {
        return isGuessed;
    }

}
