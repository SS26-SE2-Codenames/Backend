package com.codenames.codenames.backend.serialization;

import com.codenames.codenames.backend.utility.Color;

/**
 * Represents the state of a single card, for JSON serialization.
 *
 * @param word the word on the card
 * @param color the color of the card (could also be "hidden")
 * @param isGuessed the guess state of the card
 */
public record CardDataTransferObject(String word, Color color, boolean isGuessed) {}
