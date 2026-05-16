package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.utility.Team;
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
}
