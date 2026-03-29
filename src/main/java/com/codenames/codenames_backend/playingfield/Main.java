package com.codenames.codenames_backend.playingfield;


import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GameManager gameManager = new GameManager(Color.RED);

        List<Card> cardList = gameManager.getCardList();
        for(Card card : cardList){
            System.out.println(card.getWord() + " " + card.getColor());
        }
    }
}
