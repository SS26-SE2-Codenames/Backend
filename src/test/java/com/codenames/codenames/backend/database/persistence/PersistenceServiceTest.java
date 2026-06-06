package com.codenames.codenames.backend.database.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.database.repository.LobbyRepository;
import com.codenames.codenames.backend.game.application.CardGenerator;
import com.codenames.codenames.backend.game.application.ClueValidationService;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.Card;
import com.codenames.codenames.backend.game.domain.Color;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// (Spring Docs)
// By default, tests annotated with @DataJpaTest are transactional and roll back at the end of each
// test.
// Also, only uses @Entity annotated classes
@DataJpaTest
class PersistenceServiceTest {
  private final LobbyRepository lobbyRepository;
  private PersistenceService persistenceService;
  private LobbyService mockLobbyService;
  private GameService mockGameService;
  private CardGenerator mockCardGenerator;
  private ClueValidationService mockClueValidationService;
  private GameManager gameManager;
  private List<PlayerDto> playerDtoList;
  private PlayerDto player1;
  private PlayerDto player2;

  private final String lobbyCode = "ABCDE";
  private final int maxCardAmount = 25;
  private final Color redColor = Color.RED;
  private final Team redTeam = Team.RED;
  private final Role spymaster = Role.SPYMASTER;
  private final Role operative = Role.OPERATIVE;

  // Cant use new to instantiate Interfaces --> use @Autowired
  // Spring checks their active beans (@Repository in this case) and upon matching injects it here
  @Autowired
  public PersistenceServiceTest(LobbyRepository lobbyRepository) {
    this.lobbyRepository = lobbyRepository;
  }

  @BeforeEach
  void setUp() {
    mockLobbyService = mock(LobbyService.class);
    mockGameService = mock(GameService.class);
    PersistenceMapper persistenceMapper = new PersistenceMapper();

    persistenceService =
        new PersistenceService(
            mockLobbyService, mockGameService, persistenceMapper, lobbyRepository);

    mockCardGenerator = mock(CardGenerator.class);
    mockClueValidationService = mock(ClueValidationService.class);

    gameManager = helperMethodGenerateFullCardList(redColor, redTeam);

    player1 = new PlayerDto("Test1", redTeam, spymaster, true);
    player2 = new PlayerDto("Test2", redTeam, operative, false);

    playerDtoList = List.of(player1, player2);

    // controlling the saveSnapShot internal methods
    when(mockGameService.getGameState(lobbyCode)).thenReturn(gameManager);
    when(mockLobbyService.getPlayersDto(lobbyCode)).thenReturn(playerDtoList);
  }

  private void mockCardGeneration(List<Card> cardList) {
    when(mockCardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(cardList);
  }

  private GameManager helperMethodGenerateFullCardList(Color cardColor, Team startingTeam) {
    List<Card> cardList = new ArrayList<>();
    for (int i = 0; i < maxCardAmount; i++) {
      cardList.add(new Card("Test" + i, cardColor));
    }
    mockCardGeneration(cardList);
    return new GameManager(startingTeam, mockCardGenerator, mockClueValidationService);
  }

  @Test
  void testSaveSnapshot_lobbyEntity() {
    persistenceService.saveSnapShot(lobbyCode);

    LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyCode).get();
    assertNotNull(lobbyEntity);
    assertEquals(lobbyCode, lobbyEntity.getLobbyCode());
  }

  @Test
  void testSaveSnapshot_gameStateEntity() {
    persistenceService.saveSnapShot(lobbyCode);

    LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyCode).get();
    GameStateEntity gameStateEntity = lobbyEntity.getGameStateEntity();

    assertNotNull(gameStateEntity);
    assertEquals(lobbyCode, gameStateEntity.getLobbyCode());
    assertEquals("RED", gameStateEntity.getCurrentTurn());
    assertEquals("SPYMASTER", gameStateEntity.getCurrentPhase());
    assertNull(gameStateEntity.getClueWord());
    assertEquals(0, gameStateEntity.getClueGuessAmount());
    assertEquals(0, gameStateEntity.getRemainingGuesses());
  }

  @Test
  void testSaveSnapshot_player() {
    persistenceService.saveSnapShot(lobbyCode);

    LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyCode).get();
    List<PlayerEntity> playerList = lobbyEntity.getPlayerEntities();
    assertEquals(2, playerList.size());

    PlayerEntity playerEntity = playerList.get(0);

    assertEquals(lobbyCode, playerEntity.getLobbyEntity().getLobbyCode());
    assertEquals("Test1", playerEntity.getUsername());
    assertTrue(playerEntity.getIsHost());
    assertEquals("RED", playerEntity.getTeam());
    assertEquals("SPYMASTER", playerEntity.getRole());
  }

  @Test
  void testSaveSnapshot_card() {
    persistenceService.saveSnapShot(lobbyCode);

    LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyCode).get();
    List<CardEntity> cardList = lobbyEntity.getCardEntities();

    assertEquals(25, cardList.size());
    assertEquals(lobbyCode, cardList.get(0).getLobbyEntity().getLobbyCode());

    assertEquals(0, cardList.get(0).getPosition());
    assertEquals("Test0", cardList.get(0).getWord());
    assertEquals("RED", cardList.get(0).getColor());
    assertFalse(cardList.get(0).getIsGuessed());

    assertEquals(24, cardList.get(24).getPosition());
    assertEquals("Test24", cardList.get(24).getWord());
    assertEquals("RED", cardList.get(24).getColor());
    assertFalse(cardList.get(24).getIsGuessed());
  }
}
