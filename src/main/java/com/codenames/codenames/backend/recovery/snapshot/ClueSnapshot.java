package com.codenames.codenames.backend.recovery.snapshot;

/**
 * Persisted clue payload used for restart recovery snapshots.
 *
 * @param word clue word
 * @param guessAmount clue guess amount
 */
public record ClueSnapshot(String word, int guessAmount) {}
