package com.codenames.codenames_backend.serialization;

import com.codenames.codenames_backend.playingfield.Card;
import java.util.List;

/**
 * Represents the current state of the game to be serialized into JSON.
 *
 * @param currentRedFound the amount of red cards revealed
 * @param currentBlueFound the amount of red cards revealed
 * @param cardList the cards on the board
 * @param winner the winner
 * @param currentTurn the current team who is allowed to make a move
 */
public record GameStateDataTransferObject(
    int currentRedFound,
    int currentBlueFound,
    List<Card> cardList,
    String winner,
    String currentTurn) {}
