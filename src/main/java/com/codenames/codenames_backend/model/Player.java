package com.codenames.codenames_backend.model;

import com.codenames.codenames_backend.model.enums.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    // Getter and Setter
    private final String id;
    private final String name;
    private final Team team;
    @Setter
    private boolean isSpymaster;

    public Player(String id, String name, Team team, boolean isSpymaster) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.isSpymaster = isSpymaster;
    }

}

