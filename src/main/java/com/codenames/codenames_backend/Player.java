package com.codenames.codenames_backend;

public class Player {

    private String name;
    private Role role;
    private Team team;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public Team getTeam() {
        return team;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}