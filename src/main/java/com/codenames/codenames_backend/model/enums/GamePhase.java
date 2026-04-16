package com.codenames.codenames_backend.model.enums;
/**
 * Represents the different phases of a turn in Codenames.
 */
public enum GamePhase {

    /** Spymaster is giving a clue (one word + number) */
    SPYMASTER_CLUE,

    /** Operatives are guessing cards on the board */
    OPERATIVES_GUESSING,

    /** Game has finished (one team won) */
    GAME_OVER
}
