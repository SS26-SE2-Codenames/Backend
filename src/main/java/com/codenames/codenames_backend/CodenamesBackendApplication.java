package com.codenames.codenames_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodenamesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodenamesBackendApplication.class, args);

        Game game = new Game();

        game.addPlayer(new Player("Emre"));
        game.addPlayer(new Player("Ali"));
        game.addPlayer(new Player("Max"));
        game.addPlayer(new Player("Lukas"));
        game.addPlayer(new Player("Jonas"));

        game.assignTeamsAndRoles();
        game.decideStartingTeam();
    }
}