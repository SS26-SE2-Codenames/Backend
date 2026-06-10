package com.codenames.codenames.backend.game.domain;

/**
 * Result of a cheat attempt.
 *
 * @param message private message for the player
 */
public record CheatResult(String message) {}