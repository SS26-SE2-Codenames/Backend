package com.codenames.codenames.backend.database.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersistenceMappingTest {
  private PersistenceMapper persistenceMapper;
  private String lobbyCode;
  CardGenerator cardGenerator;
  ClueValidationService clueValidationService;
  GameManager gameManager;
  PlayerDto player1;
  PlayerDto player2;
  List<PlayerDto> playerDtoList;
  LobbyEntity lobbyEntity;

  @BeforeEach
  void setup() {
    persistenceMapper = new PersistenceMapper();
    lobbyCode = "ABCDE";

    cardGenerator = mock(CardGenerator.class);

    clueValidationService = mock(ClueValidationService.class);
    gameManager = new GameManager(Team.RED, cardGenerator, clueValidationService);
    player1 = new PlayerDto("Test1", Team.RED, Role.SPYMASTER, true);
    player2 = new PlayerDto("Test2", Team.RED, Role.OPERATIVE, false);
    playerDtoList = List.of(player1, player2);

    lobbyEntity =
        persistenceMapper.mapAggregateParentLobbyEntity(lobbyCode, gameManager, playerDtoList);
  }

  @Test
  void testMapPlayer() {
    List<PlayerEntity> playerEntities = lobbyEntity.getPlayerEntities();

    PlayerEntity player1 = playerEntities.get(0);

    assertEquals(lobbyEntity, player1.getLobbyEntity());
    assertEquals("Test1", player1.getUsername());
    assertTrue(player1.getIsHost());
    assertEquals("RED", player1.getTeam());
    assertEquals("SPYMASTER", player1.getRole());
  }

  
}
