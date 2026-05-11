package com.codenames.codenames_backend.model.action;

/**
 * Action for operatives.
 *
 * @param cardIndex index of the selected card
 */
public record GuessCardAction(int cardIndex) implements GameAction {
}