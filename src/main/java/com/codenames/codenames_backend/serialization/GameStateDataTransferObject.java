package com.codenames.codenames_backend.serialization;

import java.util.List;

/**
 * Represents the current state of the game to be serialized into JSON.
 *
 * @param winner the winner
 * @param currentTurn the current team who is allowed to make a move
 * @param currentRedFound the amount of red cards revealed
 * @param currentBlueFound the amount of red cards revealed
 * @param cardList the cards on the board
 */
public record GameStateDataTransferObject(
    String winner,
    String currentTurn,
    int currentRedFound,
    int currentBlueFound,
    List<CardDataTransferObject> cardList) {}
