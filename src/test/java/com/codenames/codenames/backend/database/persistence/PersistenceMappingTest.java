package com.codenames.codenames.backend.database.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
  CardGenerator mockCardGenerator;
  ClueValidationService mockClueValidationService;
  GameManager gameManager;
  PlayerDto player1;
  PlayerDto player2;
  List<PlayerDto> playerDtoList;
  LobbyEntity lobbyEntity;

  @BeforeEach
  void setup() {
    persistenceMapper = new PersistenceMapper();
    lobbyCode = "ABCDE";

    mockCardGenerator = mock(CardGenerator.class);

    mockClueValidationService = mock(ClueValidationService.class);
    gameManager = new GameManager(Team.RED, mockCardGenerator, mockClueValidationService);
    player1 = new PlayerDto("Test1", Team.RED, Role.SPYMASTER, true);
    player2 = new PlayerDto("Test2", Team.RED, Role.OPERATIVE, false);
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
    for (int i = 0; i < 25; i++) {
      cardList.add(new Card("Test" + i, cardColor));
    }
    mockCardGeneration(cardList);
    GameManager fullListGameManager =
        new GameManager(startingTeam, mockCardGenerator, mockClueValidationService);
    return fullListGameManager;
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
  void testMapCard() {
    GameManager fullCardGameManager = helperMethodGenerateFullCardList(Color.RED, Team.RED);
    LobbyEntity fullCardLobbyEntity =
        persistenceMapper.mapAggregateParentLobbyEntity(
            lobbyCode, fullCardGameManager, playerDtoList);
    List<CardEntity> cardEntities = fullCardLobbyEntity.getCardEntities();

    for (int i = 0; i < 25; i++) {
      CardEntity cardEntity = cardEntities.get(i);
      assertEquals(fullCardLobbyEntity, cardEntity.getLobbyEntity());
      assertEquals(i, cardEntity.getPosition());
      assertEquals("Test" + i, cardEntity.getWord());
      assertEquals("RED", cardEntity.getColor());
      assertFalse(cardEntity.getIsGuessed());
    }
  }
}
