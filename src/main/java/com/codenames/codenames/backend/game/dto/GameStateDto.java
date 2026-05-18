package com.codenames.codenames.backend.game.dto;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;

/**
 * DTO representing the current game state.
 */
public record GameStateDto(
        List<Card> cards,
        Clue currentClue,
        int remainingGuesses,
        Team winner,
        Team currentTurn,
        Role currentPhase
) {}
