package com.codenames.codenames_backend.model.action;

/**
 * Action for the spymaster.
 *
 * @param clueWord clue word
 * @param number number of cards connected to the clue
 */
public record GiveClueAction(String clueWord, int number) implements GameAction {
}