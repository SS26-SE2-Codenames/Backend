package com.codenames.codenames.backend.serialization;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.game.dto.ClueDto;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;

/**
 * Represents the current state of the game to be serialized into JSON.
 *
 * @param winner the winner
 * @param currentTurn the current team who is allowed to make a move
 * @param currentClue the current clue object, consisting of word and amount of guesses
 * @param cardList the cards on the board
 */
public record GameStateDataTransferObject(
    Team winner,
    Team currentTurn,
    Role currentPhase,
    ClueDto currentClue,
    List<CardDataTransferObject> cardList) {}
