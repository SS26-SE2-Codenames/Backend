package com.codenames.codenames_backend.service;

import com.codenames.codenames_backend.exception.IllegalGameActionException;
import com.codenames.codenames_backend.model.*;
import com.codenames.codenames_backend.model.action.*;
import com.codenames.codenames_backend.model.enums.GamePhase;
import com.codenames.codenames_backend.model.enums.Team;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing turns and game flow (Backend #11)
 */
@Service
public class TurnService {
    /**
     * Processes a player's action and returns the updated game state.
     */
    public GameState processAction(GameState state, Player player, GameAction action,
                                   Player redSpymaster, Player blueSpymaster) {

        if (state.getCurrentPhase() == GamePhase.GAME_OVER) {
            throw new IllegalGameActionException("The game is already over.");
        }

        // Spymaster gives a clue
        if (action instanceof GiveClueAction clueAction) {
            return handleGiveClue(state, player, clueAction);
        }
        // Operative guesses a card
        else if (action instanceof GuessCardAction guessAction) {
            return handleGuessCard(state, player, guessAction, redSpymaster,
                    blueSpymaster);
        }
        // Player passes the turn
        else if (action instanceof PassAction) {
            return handlePass(state);
        }
        throw new IllegalGameActionException("Unknown Action type.");
    }

    private GameState handlePass(GameState state) {
        return state;
    }

    private GameState handleGiveClue(GameState state, Player player,
                                     GiveClueAction action) throws IllegalGameActionException {
        if (state.getCurrentPhase() != GamePhase.SPYMASTER_CLUE) {
            throw new IllegalGameActionException("It is not Spymaster's turn to give a clue.");
        }
        if (!player.isSpymaster() || !player.equals(state.getCurrentSpymaster())) {
            throw new IllegalGameActionException("Only the current Spymaster can give a clue.");
        }
        if (action.number() < 1 || action.number() > 9) {
            throw new IllegalGameActionException("The number must be between 1 and 9.");
        }

        state.setCurrentClue(action.clueWord());
        state.setRemainingGuesses(action.number() + 1); // +1 rule
        state.setCurrentPhase(GamePhase.OPERATIVES_GUESSING);

        return state;
    }

    private GameState handleGuessCard(GameState state, Player player, GuessCardAction action,
                                      Player redSpymaster, Player blueSpymaster) {
        Card guessedCard = getGuessedCard(state, player, action);

        // === IMPORTANT LOGIC: What kind of card was guessed? ===
        if (guessedCard.getType() == null) {
            // Neutral card -> turn ends
            state.switchTeam(redSpymaster, blueSpymaster);
        } else if (guessedCard.getType() == Team.ASSASSIN) {
            // Assassin! -> current team loses
            state.setWinner(player.getTeam() == Team.RED ? Team.BLUE : Team.RED);
            state.setCurrentPhase(GamePhase.GAME_OVER);
        } else if (guessedCard.getType() == state.getCurrentTeam()) {
            // Correct card of current team
            state.setRemainingGuesses(state.getRemainingGuesses() - 1);

            // Check if team has found all their cards
            if (isTeamComplete(state, state.getCurrentTeam())) {
                state.setWinner(state.getCurrentTeam());
                state.setCurrentPhase(GamePhase.GAME_OVER);
            } else if (state.getRemainingGuesses() <= 0) {
                state.switchTeam(redSpymaster, blueSpymaster);
            }
        } else {
            // Wrong color (opponent's card) -> turn ends
            state.switchTeam(redSpymaster, blueSpymaster);
        }

        return state;
    }

    private static Card getGuessedCard(GameState state, Player player, GuessCardAction action) {
        if (state.getCurrentPhase() != GamePhase.OPERATIVES_GUESSING) {
            throw new IllegalGameActionException("It is not the guessing phase. ");
        }
        if (player.getTeam() != state.getCurrentTeam()) {
            throw new IllegalGameActionException("You are not part of the current team. ");
        }

        int index = action.cardIndex();
        List<Card> cards = state.getCards();

        if (index < 0 || index >= cards.size()) {
            throw new IllegalGameActionException("Invalid card index.");
        }

        Card guessedCard = cards.get(index);

        // Reveal card
        guessedCard.reveal();
        return guessedCard;
    }

    /**
     * Cheks if a team revealed all their.cards.
     */

    private boolean isTeamComplete(GameState state, Team team) {
        long remaining = state.getCards().stream()
                .filter(card -> !card.isRevealed() && card.getType() == team)
                .count();
        return remaining == 0;
    }
}



