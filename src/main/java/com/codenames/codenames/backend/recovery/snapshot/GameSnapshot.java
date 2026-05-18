package com.codenames.codenames.backend.recovery.snapshot;

import com.codenames.codenames.backend.serialization.CardDataTransferObject;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;

/**
 * Persisted game payload used for restart recovery snapshots.
 *
 * @param currentTurn current team turn
 * @param currentPhase current gameplay phase
 * @param winner winner if already decided
 * @param currentRedFound discovered red cards
 * @param currentBlueFound discovered blue cards
 * @param remainingGuesses remaining guesses
 * @param currentClue current clue if present
 * @param cards full card state list
 */
public record GameSnapshot(
    Team currentTurn,
    Role currentPhase,
    Team winner,
    int currentRedFound,
    int currentBlueFound,
    int remainingGuesses,
    ClueSnapshot currentClue,
    List<CardDataTransferObject> cards) {}
