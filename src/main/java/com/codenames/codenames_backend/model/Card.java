package com.codenames.codenames_backend.model;

import com.codenames.codenames_backend.model.enums.Team;
import lombok.Getter;

@Getter
public class Card {
    private final String word;
    private final Team type;     // RED, BLUE, NEUTERAl (null) or ASSASSIN
    private boolean revealed;

    public Card(String word, Team type) {
        this.word = word;
        this.type = type;
        this.revealed = false;
    }

    public void reveal() {
        this.revealed = true;
    }
}
