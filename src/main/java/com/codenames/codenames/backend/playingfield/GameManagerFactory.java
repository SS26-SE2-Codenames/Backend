package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.game.dto.ClueDto;
import com.codenames.codenames.backend.serialization.CardDataTransferObject;
import com.codenames.codenames.backend.serialization.GameStateDataTransferObject;
import com.codenames.codenames.backend.utility.Color;
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

  /**
   * Recreates a {@link GameManager} from a persisted snapshot.
   *
   * @param snapshot persisted game state snapshot
   * @return restored game manager
   */
  public GameManager createFromSnapshot(GameStateDataTransferObject snapshot) {
    ClueDto clueDto = snapshot.currentClue();
    Clue clue = clueDto == null ? null : new Clue(clueDto.word(), clueDto.guessAmount());
    List<Card> cards = snapshot.cardList().stream().map(this::toCard).toList();
    int recoveredRedFound = countGuessedByColor(cards, Color.RED);
    int recoveredBlueFound = countGuessedByColor(cards, Color.BLUE);
    GameRecoveryState recoveryState =
        new GameRecoveryState(
            cards,
            snapshot.currentTurn(),
            snapshot.currentPhase(),
            snapshot.winner(),
            recoveredRedFound,
            recoveredBlueFound,
            snapshot.remainingGuesses(),
            clue);

    return new GameManager(recoveryState, clueValidationService);
  }

  /**
   * Maps persisted card DTO representation to runtime {@link Card}.
   *
   * @param cardDto persisted card payload
   * @return runtime card instance
   */
  private Card toCard(CardDataTransferObject cardDto) {
    Card card = new Card(cardDto.word(), cardDto.color());
    if (cardDto.isGuessed()) {
      card.setIsGuessedTrue();
    }
    return card;
  }

  private int countGuessedByColor(List<Card> cards, Color color) {
    return (int)
        cards.stream().filter(card -> card.isGuessed() && card.getColor() == color).count();
  }
}
