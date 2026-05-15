package com.codenames.codenames.backend.playingfield;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.utility.Team;
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
}
