package com.codenames.codenames_backend;

import lombok.Getter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Game {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final List<Player> players = new ArrayList<>();
    private Team startingTeam;

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void assignTeamsAndRoles() {
        if (players.size() < 4) {
            return;
        }

        Collections.shuffle(players);

        int totalPlayers = players.size();

        int redSize = (totalPlayers + 1) / 2;

        List<Player> redTeam = new ArrayList<>();
        List<Player> blueTeam = new ArrayList<>();

        for (int i = 0; i < redSize; i++) {
            players.get(i).setTeam(Team.RED);
            redTeam.add(players.get(i));
        }

        for (int i = redSize; i < totalPlayers; i++) {
            players.get(i).setTeam(Team.BLUE);
            blueTeam.add(players.get(i));
        }

        if (redTeam.size() < 2 || blueTeam.size() < 2) {
            return;
        }

        redTeam.get(0).setRole(Role.SPYMASTER);
        for (int i = 1; i < redTeam.size(); i++) {
            redTeam.get(i).setRole(Role.OPERATIVE);
        }

        blueTeam.get(0).setRole(Role.SPYMASTER);
        for (int i = 1; i < blueTeam.size(); i++) {
            blueTeam.get(i).setRole(Role.OPERATIVE);
        }
    }

    public void decideStartingTeam() {
        if (RANDOM.nextBoolean()) {
            startingTeam = Team.RED;
        } else {
            startingTeam = Team.BLUE;
        }
    }

}