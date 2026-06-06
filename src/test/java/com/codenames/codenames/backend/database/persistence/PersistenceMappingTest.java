package com.codenames.codenames.backend.database.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.game.application.CardGenerator;
import com.codenames.codenames.backend.game.application.ClueValidationService;
import com.codenames.codenames.backend.game.domain.Card;
import com.codenames.codenames.backend.game.domain.Clue;
import com.codenames.codenames.backend.game.domain.Color;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersistenceMappingTest {
  private PersistenceMapper persistenceMapper;
  private String lobbyCode;
  private CardGenerator mockCardGenerator;
  private ClueValidationService mockClueValidationService;
  private GameManager gameManager;
  private PlayerDto player1;
  private PlayerDto player2;
  private List<PlayerDto> playerDtoList;
  private LobbyEntity lobbyEntity;
  private final Color redColor = Color.RED;
  private final Team redTeam = Team.RED;
  private final Role spymaster =  Role.SPYMASTER;
  private final Role operative =  Role.OPERATIVE;
  int maxCardAmount = 25;

  @BeforeEach
  void setup() {
    persistenceMapper = new PersistenceMapper();
    lobbyCode = "ABCDE";

    mockCardGenerator = mock(CardGenerator.class);

    mockClueValidationService = mock(ClueValidationService.class);
    gameManager = new GameManager(redTeam, mockCardGenerator, mockClueValidationService);
    player1 = new PlayerDto("Test1", redTeam, spymaster, true);
    player2 = new PlayerDto("Test2", redTeam, operative, false);
    playerDtoList = List.of(player1, player2);

    lobbyEntity =
        persistenceMapper.mapAggregateParentLobbyEntity(lobbyCode, gameManager, playerDtoList);
  }

  // Modified helper methods from GameManager to generate a game with full cards
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
  void testMapPlayer() {
    List<PlayerEntity> playerEntities = lobbyEntity.getPlayerEntities();

    PlayerEntity retrievedPlayer1 = playerEntities.get(0);

    assertEquals(lobbyEntity, retrievedPlayer1.getLobbyEntity());
    assertEquals("Test1", retrievedPlayer1.getUsername());
    assertTrue(retrievedPlayer1.getIsHost());
    assertEquals("RED", retrievedPlayer1.getTeam());
    assertEquals("SPYMASTER", retrievedPlayer1.getRole());
  }

  @Test
  void testMapGameState() {
    GameStateEntity gameStateEntity = lobbyEntity.getGameStateEntity();
    assertEquals(lobbyEntity, gameStateEntity.getLobbyEntity());
    assertEquals(lobbyCode, gameStateEntity.getLobbyCode());
    assertEquals("RED", gameStateEntity.getCurrentTurn());
    assertEquals("SPYMASTER", gameStateEntity.getCurrentPhase());
    assertNull(gameStateEntity.getClueWord());
    assertEquals(0, gameStateEntity.getClueGuessAmount());
    assertEquals(0, gameStateEntity.getRemainingGuesses());
  }

  @Test
  void testMapGameState_notNullClue() {
    GameManager gameManagerWithClue = helperMethodGenerateFullCardList(redColor, redTeam);
    when(mockClueValidationService.validateWord(any(), any())).thenReturn(true);
    gameManager.submitClue(new Clue("TestWord", 1), redTeam);
    lobbyEntity =
        persistenceMapper.mapAggregateParentLobbyEntity(lobbyCode, gameManager, playerDtoList);

    GameStateEntity gameStateEntity = lobbyEntity.getGameStateEntity();
    assertEquals("TestWord", gameStateEntity.getClueWord());
    assertEquals(2, gameStateEntity.getClueGuessAmount());
    assertEquals(2, gameStateEntity.getRemainingGuesses());
  }

  @Test
  void testMapCard() {
    GameManager fullCardGameManager = helperMethodGenerateFullCardList(redColor, redTeam);
    LobbyEntity fullCardLobbyEntity =
        persistenceMapper.mapAggregateParentLobbyEntity(
            lobbyCode, fullCardGameManager, playerDtoList);
    List<CardEntity> cardEntities = fullCardLobbyEntity.getCardEntities();

    for (int i = 0; i < maxCardAmount; i++) {
      CardEntity cardEntity = cardEntities.get(i);
      assertEquals(fullCardLobbyEntity, cardEntity.getLobbyEntity());
      assertEquals(i, cardEntity.getPosition());
      assertEquals("Test" + i, cardEntity.getWord());
      assertEquals("RED", cardEntity.getColor());
      assertFalse(cardEntity.getIsGuessed());
    }
  }
}
