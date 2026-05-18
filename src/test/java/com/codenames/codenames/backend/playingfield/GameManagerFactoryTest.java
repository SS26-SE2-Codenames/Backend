package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.recovery.snapshot.ClueSnapshot;
import com.codenames.codenames.backend.recovery.snapshot.GameSnapshot;
import com.codenames.codenames.backend.serialization.CardDataTransferObject;
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
    GameSnapshot snapshot =
        new GameSnapshot(
            Team.RED,
            Role.OPERATIVE,
            null,
            1,
            0,
            2,
            new ClueSnapshot("ANIMAL", 2),
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
    GameSnapshot snapshot =
        new GameSnapshot(
            Team.BLUE,
            Role.SPYMASTER,
            null,
            0,
            0,
            0,
            null,
            List.of(new CardDataTransferObject("Tree", Color.BLUE, false)));

    GameManager recovered = gameManagerFactory.createFromSnapshot(snapshot);

    assertNotNull(recovered);
    assertNull(recovered.getCurrentClue());
  }
}
