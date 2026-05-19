package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.game.dto.ClueDto;
import com.codenames.codenames.backend.serialization.CardDataTransferObject;
import com.codenames.codenames.backend.serialization.GameStateDataTransferObject;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Role;
import com.codenames.codenames.backend.utility.Team;
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
    GameStateDataTransferObject snapshot =
        new GameStateDataTransferObject(
            null,
            Team.RED,
            Role.OPERATIVE,
            new ClueDto("ANIMAL", 2),
            2,
            List.of(
                new CardDataTransferObject("Dog", Color.RED, true),
                new CardDataTransferObject("Cat", Color.BLUE, false)));

    GameManager recovered = gameManagerFactory.createFromSnapshot(snapshot);

    assertNotNull(recovered);
    assertTrue(recovered.getCardList().get(0).isGuessed());
    assertEquals(2, recovered.getRemainingGuesses());
    assertEquals("ANIMAL", recovered.getCurrentClueWord());
  }

  @Test
  void testCreateFromSnapshotWithoutClue() {
    GameStateDataTransferObject snapshot =
        new GameStateDataTransferObject(
            null, Team.BLUE, Role.SPYMASTER, null, 0,
            List.of(new CardDataTransferObject("Tree", Color.BLUE, false)));

    GameManager recovered = gameManagerFactory.createFromSnapshot(snapshot);

    assertNotNull(recovered);
    assertNull(recovered.getCurrentClue());
  }
}
