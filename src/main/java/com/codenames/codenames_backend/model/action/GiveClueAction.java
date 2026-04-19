package com.codenames.codenames_backend.model.action;

public record GiveClueAction(String clueWord, int number) implements GameAction {

    public String getClueWord() {
    }
}
