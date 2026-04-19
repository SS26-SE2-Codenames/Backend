package com.codenames.codenames_backend.controller;

import com.codenames.codenames_backend.model.GameState;
import com.codenames.codenames_backend.model.action.GiveClueAction;
import com.codenames.codenames_backend.model.action.GuessCardAction;
import com.codenames.codenames_backend.model.action.PassAction;
import com.codenames.codenames_backend.service.TurnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *  REST Controller for game turn management (Backend #11)
 */
@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")  // Allows frontend access later
public class GameController {

    private final TurnService turnService;

    public GameController(TurnService turnService) {
        this.turnService = turnService;
    }

    /**
     * Spymaster give a clue
     */
    @PostMapping("/clue")
    public ResponseEntity<GameState> giveClue(@RequestBody GiveClueAction action) {
        // For Sprint 1: Dummy-GameState (later we will load real game from repository)
        GameState state = new GameState(/* later from Repository */);
        GameState updated = turnService.processAction(state, null, action, null, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Operative guesses a card
     */
    @PostMapping("/guess")
    public ResponseEntity<GameState> guessCard(@RequestBody GuessCardAction action) {
        GameState state = new GameState(/* later from Repository */);
        GameState updated = turnService.processAction(state, null, action, null, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Player passes the run
     */
    @PostMapping("/pass")
    public ResponseEntity<GameState> passTurn() {
        GameState state = new GameState(/* later from Repository */);
        GameState updated = turnService.processAction(state, null, new PassAction(), null, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get current game state
     */
    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState() {
        GameState state = new GameState(/* later from Repository */);
        return ResponseEntity.ok(state);
    }
}

