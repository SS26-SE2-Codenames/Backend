package com.codenames.codenames.backend.game.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.codenames.codenames.backend.game.api.dto.CardDto;
import com.codenames.codenames.backend.game.api.dto.ClueDto;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.application.CardGenerator;
import com.codenames.codenames.backend.game.application.ClueValidationService;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameManagerFactoryTest {

  private GameManagerFactory gameManagerFactory;

  @BeforeEach
  void setup() {
    CardGenerator mockCardGenerator = mock(CardGenerator.class);
    ClueValidationService mockClueValidationService = mock(ClueValidationService.class);

    gameManagerFactory = new GameManagerFactory(mockCardGenerator, mockClueValidationService);
  }

  @Test
  void testCreate() {
    GameManager gameManager = gameManagerFactory.create(Team.RED);

    assertNotNull(gameManager);
  }

  @Test
  void testCreateFromSnapshotWithClue() {
    GameStateDto snapshot =
        new GameStateDto(
            Team.RED,
            Team.BLUE,
            Role.OPERATIVE,
            new ClueDto("ANIMAL", 2),
            List.of(new CardDto("Dog", Color.RED, true), new CardDto("Cat", Color.BLUE, false)), 2);

    GameManager recovered = gameManagerFactory.createFromSnapshot(snapshot);

    assertNotNull(recovered);
    assertEquals(Team.BLUE, recovered.getCurrentTurn());
    assertEquals(Role.OPERATIVE, recovered.getCurrentPhase());
    assertEquals("ANIMAL", recovered.getCurrentClueWord());
    assertEquals(1, recovered.getCurrentRedFound());
    assertEquals(0, recovered.getCurrentBlueFound());
    assertEquals(Team.RED, recovered.getWinner());
    assertTrue(recovered.getCardList().get(0).isGuessed());
  }

  @Test
  void testCreateFromSnapshotWithoutClue() {
    GameStateDto snapshot =
        new GameStateDto(
            null, Team.BLUE, Role.SPYMASTER, null, List.of(new CardDto("Tree", Color.BLUE, false)), 2);

    GameManager recovered = gameManagerFactory.createFromSnapshot(snapshot);

    assertNotNull(recovered);
    assertEquals(Team.BLUE, recovered.getCurrentTurn());
    assertEquals(Role.SPYMASTER, recovered.getCurrentPhase());
    assertNull(recovered.getCurrentClue());
  }

  @Test
  void testCreateFromSnapshotMapsCardsAndCountsGuessedCardsByColor() {
    GameStateDto snapshot =
        new GameStateDto(
            null,
            Team.RED,
            Role.OPERATIVE,
            null,
            List.of(
                new CardDto("Dog", Color.RED, true),
                new CardDto("Cat", Color.RED, false),
                new CardDto("Tree", Color.BLUE, true),
                new CardDto("Sun", Color.NEUTRAL, true)),
                2);

    GameManager recovered = gameManagerFactory.createFromSnapshot(snapshot);

    assertEquals(4, recovered.getCardList().size());

    assertEquals("Dog", recovered.getCardList().get(0).getWord());
    assertEquals(Color.RED, recovered.getCardList().get(0).getColor());
    assertTrue(recovered.getCardList().get(0).isGuessed());

    assertEquals("Cat", recovered.getCardList().get(1).getWord());
    assertEquals(Color.RED, recovered.getCardList().get(1).getColor());

    assertEquals("Tree", recovered.getCardList().get(2).getWord());
    assertEquals(Color.BLUE, recovered.getCardList().get(2).getColor());
    assertTrue(recovered.getCardList().get(2).isGuessed());

    assertEquals(1, recovered.getCurrentRedFound());
    assertEquals(1, recovered.getCurrentBlueFound());
  }
}
