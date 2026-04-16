package com.codenames.codenames_backend.model.action;
/**
 *  Common interface for all possible actions in the game.
 *  Used to represent player actions such as:
 *  - Giving a clue (Spymaster)
 *  - Guessing a card (Operatives)
 *  - Passing the turn.
 */
public interface GameAction {
    int getNumber();

    int getCardIndex();
}
