package com.codenames.codenames.backend.game.api.dto;

import com.codenames.codenames.backend.game.domain.Color;

/**
 * Represents the state of a single card, for JSON serialization.
 *
 * @param word the word on the card
 * @param color the color of the card (could also be "hidden")
 * @param isGuessed the guess state of the card
 */
public record CardDataTransferObject(String word, Color color, boolean isGuessed) {}
