package com.codenames.codenames.backend.game.domain;

import com.codenames.codenames.backend.lobby.domain.Team;

/**
 * Result of an expose-cheat attempt.
 *
 * @param correct whether the opposing team had used their cheat
 * @param team the team that tried to expose the cheat
 */
public record ExposeCheatResult(
    boolean correct,
    Team team) {
}
