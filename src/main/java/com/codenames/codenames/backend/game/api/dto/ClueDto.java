package com.codenames.codenames.backend.game.api.dto;

/**
 * Data transfer object for a clue, containing the clue word and the allowed number of guesses.
 * This is only used for sending clues, not for receiving them.
 * Therefore, the +1 is already included in the guessAmount.
 *
 * @param word the hint word
 * @param guessAmount how many guesses are allowed
 */
public record ClueDto(String word, int guessAmount) {
}
