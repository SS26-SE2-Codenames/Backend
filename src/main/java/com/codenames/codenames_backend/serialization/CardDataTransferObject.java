package com.codenames.codenames_backend.serialization;

/**
 * Represents the state of a single card, for JSON serialization.
 *
 * @param word the word on the card
 * @param color the color of the card (could also be "hidden")
 * @param isGuessed the guess state of the card
 */
public record CardDataTransferObject(String word, String color, boolean isGuessed) {}
