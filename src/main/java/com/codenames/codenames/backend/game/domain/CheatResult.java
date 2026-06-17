package com.codenames.codenames.backend.game.domain;

import com.codenames.codenames.backend.lobby.domain.Team;
/**
 * Result of a cheat attempt.
 *
 * @param message private message for the player
 * @param team the team that used the cheat
 */
public record CheatResult(
    String message,
    Team team) {}