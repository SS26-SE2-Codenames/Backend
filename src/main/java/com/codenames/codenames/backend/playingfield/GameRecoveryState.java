package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;

/**
 * Compact recovery payload used to rebuild a {@link GameManager} after restart.
 *
 * @param cards recovered board cards
 * @param currentTurn recovered active team
 * @param currentPhase recovered active role phase
 * @param winner recovered winner if game already ended
 * @param currentRedFound recovered count of discovered red cards
 * @param currentBlueFound recovered count of discovered blue cards
 * @param remainingGuesses recovered remaining guesses
 * @param currentClue recovered clue if present
 */
public record GameRecoveryState(
    List<Card> cards,
    Team currentTurn,
    Role currentPhase,
    Team winner,
    int currentRedFound,
    int currentBlueFound,
    int remainingGuesses,
    Clue currentClue) {}
