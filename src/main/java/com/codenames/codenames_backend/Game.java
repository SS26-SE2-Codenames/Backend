package com.codenames.codenames_backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private List<Player> players = new ArrayList<>();
    private Team startingTeam;

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void assignTeamsAndRoles() {
        if (players.size() < 4) {
            System.out.println("Es werden mindestens 4 Spieler benötigt.");
            return;
        }

        Collections.shuffle(players);

        int totalPlayers = players.size();

        // RED bekommt die größere Hälfte wenn ungerade
        int redSize = (totalPlayers + 1) / 2;
        int blueSize = totalPlayers - redSize;

        List<Player> redTeam = new ArrayList<>();
        List<Player> blueTeam = new ArrayList<>();

        // Teams aufteilen
        for (int i = 0; i < redSize; i++) {
            players.get(i).setTeam(Team.RED);
            redTeam.add(players.get(i));
        }

        for (int i = redSize; i < totalPlayers; i++) {
            players.get(i).setTeam(Team.BLUE);
            blueTeam.add(players.get(i));
        }

        // Sicherheit (falls jemand später Mist baut 😄)
        if (redTeam.size() < 2 || blueTeam.size() < 2) {
            System.out.println("Jedes Team braucht mindestens 2 Spieler!");
            return;
        }

        // Rollen vergeben
        redTeam.get(0).setRole(Role.SPYMASTER);
        for (int i = 1; i < redTeam.size(); i++) {
            redTeam.get(i).setRole(Role.OPERATIVE);
        }

        blueTeam.get(0).setRole(Role.SPYMASTER);
        for (int i = 1; i < blueTeam.size(); i++) {
            blueTeam.get(i).setRole(Role.OPERATIVE);
        }
    }

    public void printPlayers() {
        for (Player player : players) {
            System.out.println(
                    player.getName() + " -> Team: " + player.getTeam() + " -> Rolle: " + player.getRole()
            );
        }

        System.out.println("Startendes Team: " + startingTeam);
    }

    public void decideStartingTeam() {
        if (Math.random() < 0.5) {
            startingTeam = Team.RED;
        } else {
            startingTeam = Team.BLUE;
        }
    }

    public Team getStartingTeam() {
        return startingTeam;
    }

    public List<Player> getPlayers() {
        return players;
    }
}