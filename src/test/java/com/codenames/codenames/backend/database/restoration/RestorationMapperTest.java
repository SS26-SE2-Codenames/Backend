package com.codenames.codenames.backend.database.restoration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestorationMapperTest {
  private RestorationMapper restorationMapper;
  private LobbyEntity lobbyEntity;

  @BeforeEach
  void setUp() {
    restorationMapper = new RestorationMapper();
    lobbyEntity = new LobbyEntity();
    lobbyEntity.setLobbyCode("ABCDE");
  }

  @Test
  void testMapToLobbyDto() {
    PlayerEntity playerEntity = new PlayerEntity();
    playerEntity.setLobbyEntity(lobbyEntity);
    playerEntity.setUsername("Player1");
    playerEntity.setIsHost(true);
    playerEntity.setTeam("RED");
    playerEntity.setRole("OPERATIVE");
    List<PlayerEntity> playerList = List.of(playerEntity);
    lobbyEntity.setPlayerEntities(playerList);

    Lobby lobby = restorationMapper.mapToLobby(lobbyEntity);

    assertNotNull(lobby);
    assertEquals("ABCDE", lobby.getLobbyCode());

    assertEquals(1, lobby.getPlayerList().size());
  }

  
}
