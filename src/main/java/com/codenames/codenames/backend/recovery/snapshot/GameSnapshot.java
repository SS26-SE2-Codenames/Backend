package com.codenames.codenames.backend.recovery.snapshot;

import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;

public record GameSnapshot(
    Team currentTurn,
    Role currentPhase,
    Team winner,
    int currentRedFound,
    int currentBlueFound,
    int remainingGuesses,
    ClueSnapshot currentClue,
    List<Card> cards) {}
