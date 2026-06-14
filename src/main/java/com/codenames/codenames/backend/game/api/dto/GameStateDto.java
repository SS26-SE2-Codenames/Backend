package com.codenames.codenames.backend.game.api.dto;

import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;

/**
 * Represents the current state of the game to be serialized into JSON.
 *
 * @param winner       the winner
 * @param currentTurn  the current team who is allowed to make a move
 * @param currentPhase the current phase (spymaster or operative)
 * @param currentClue  the current clue object, consisting of word and amount of guesses
 * @param cardList     the cards on the board
 */
public record GameStateDto(
    Team winner,
    Team currentTurn,
    Role currentPhase,
    ClueDto currentClue,
    int remainingGuesses,
    List<CardDto> cardList,
    boolean redTeamCheatUsed,
    boolean blueTeamCheatUsed) {
}