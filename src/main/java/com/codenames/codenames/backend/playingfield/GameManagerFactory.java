package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.recovery.snapshot.GameSnapshot;
import com.codenames.codenames.backend.serialization.CardDataTransferObject;
import com.codenames.codenames.backend.utility.Team;
import java.util.List;
import org.springframework.stereotype.Component;

/** Generates GameManager instances to be used by GameService. */
@Component
public class GameManagerFactory {
  private final CardGenerator cardGenerator;
  private final ClueValidationService clueValidationService;

  /**
   * Initialized the factory with utility services injected via Spring.
   *
   * @param cardGenerator utility service to generate cards
   * @param clueValidationService utility service to validate clues
   */
  public GameManagerFactory(
      CardGenerator cardGenerator, ClueValidationService clueValidationService) {
    this.cardGenerator = cardGenerator;
    this.clueValidationService = clueValidationService;
  }

  /**
   * Creates a GameManager object that is used in GameService.
   *
   * @param startingTeam the team that starts the game
   * @return the GameManager object to be used in GameService
   */
  public GameManager create(Team startingTeam) {
    return new GameManager(startingTeam, cardGenerator, clueValidationService);
  }

  public GameManager createFromSnapshot(GameSnapshot snapshot) {
    Clue clue =
        snapshot.currentClue() == null
            ? null
            : new Clue(snapshot.currentClue().word(), snapshot.currentClue().guessAmount());
    List<Card> cards = snapshot.cards().stream().map(this::toCard).toList();

    return new GameManager(
        cards,
        snapshot.currentTurn(),
        snapshot.currentPhase(),
        snapshot.winner(),
        snapshot.currentRedFound(),
        snapshot.currentBlueFound(),
        snapshot.remainingGuesses(),
        clue,
        clueValidationService);
  }

  private Card toCard(CardDataTransferObject cardDto) {
    Card card = new Card(cardDto.word(), cardDto.color());
    if (cardDto.isGuessed()) {
      card.setIsGuessedTrue();
    }
    return card;
  }
}
