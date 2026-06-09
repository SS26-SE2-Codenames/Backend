package com.codenames.codenames.backend.database.restoration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.domain.Color;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestorationMapperTest {
  private RestorationMapper restorationMapper;
  private LobbyEntity lobbyEntity;
  private final Team redTeam = Team.RED;
  private final Color redColor = Color.RED;
  private final Role operativeRole = Role.OPERATIVE;

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

  @Test
  void testMapToGameDto() {
    GameStateEntity gameStateEntity = new GameStateEntity();
    gameStateEntity.setCurrentTurn("RED");
    gameStateEntity.setCurrentPhase("OPERATIVE");
    gameStateEntity.setClueWord("TestClueWord");
    gameStateEntity.setClueGuessAmount(3);
    lobbyEntity.setGameStateEntity(gameStateEntity);

    CardEntity card = new CardEntity();
    card.setWord("TestCardWord");
    card.setColor("RED");
    card.setIsGuessed(false);
    lobbyEntity.setCardEntities(List.of(card));

    GameStateDto gameStateDto = restorationMapper.mapToGameStateDto(lobbyEntity);

    assertNotNull(gameStateDto);
    assertEquals(redTeam, gameStateDto.currentTurn());
    assertEquals(operativeRole, gameStateDto.currentPhase());
    assertEquals("TestClueWord", gameStateDto.currentClue().word());
    assertEquals(1, gameStateDto.cardList().size());
    assertEquals("TestCardWord", gameStateDto.cardList().get(0).word());
    assertEquals(redColor, gameStateDto.cardList().get(0).color());
  }

  @Test
  void testMapToGameDto_null() {
    when(lobbyEntity.getGameStateEntity()).thenReturn(null);
    assertThrows(
        IllegalStateException.class, () -> restorationMapper.mapToGameStateDto(lobbyEntity));
  }

  @Test
  void testMapToGameDto_nullClueWord() {
    GameStateEntity gameStateEntity = new GameStateEntity();
    gameStateEntity.setCurrentTurn("RED");
    gameStateEntity.setCurrentPhase("OPERATIVE");
    gameStateEntity.setClueWord(null);
    gameStateEntity.setClueGuessAmount(3);
    lobbyEntity.setGameStateEntity(gameStateEntity);

    GameStateDto gameStateDto = restorationMapper.mapToGameStateDto(lobbyEntity);
    assertNull(gameStateDto.currentClue());
  }

  @Test
  void testBuildLobby_nullTeam() {
    PlayerEntity playerEntity = new PlayerEntity();
    playerEntity.setLobbyEntity(lobbyEntity);
    playerEntity.setUsername("TestPlayer");
    playerEntity.setIsHost(true);
    playerEntity.setTeam(redTeam.toString());
    playerEntity.setRole(operativeRole.toString());

    PlayerEntity playerEntity2 = new PlayerEntity();
    playerEntity2.setLobbyEntity(lobbyEntity);
    playerEntity2.setUsername("TestPlayer2");
    playerEntity2.setIsHost(false);
    playerEntity2.setTeam(null);
    playerEntity2.setRole(operativeRole.toString());
    List<PlayerEntity> playerList = List.of(playerEntity, playerEntity2);

    lobbyEntity.setPlayerEntities(playerList);

    Lobby lobby = restorationMapper.mapToLobby(lobbyEntity);

    assertNull(lobby.getPlayerTeam("TestPlayer2"));
  }

  @Test
  void testBuildLobby_nullRole() {
    PlayerEntity playerEntity = new PlayerEntity();
    playerEntity.setLobbyEntity(lobbyEntity);
    playerEntity.setUsername("TestPlayer");
    playerEntity.setIsHost(true);
    playerEntity.setTeam(redTeam.toString());
    playerEntity.setRole(operativeRole.toString());

    PlayerEntity playerEntity2 = new PlayerEntity();
    playerEntity2.setLobbyEntity(lobbyEntity);
    playerEntity2.setUsername("TestPlayer2");
    playerEntity2.setIsHost(false);
    playerEntity2.setTeam(redTeam.toString());
    playerEntity2.setRole(null);
    List<PlayerEntity> playerList = List.of(playerEntity, playerEntity2);

    lobbyEntity.setPlayerEntities(playerList);

    Lobby lobby = restorationMapper.mapToLobby(lobbyEntity);

    assertNull(lobby.getPlayerRole("TestPlayer2"));
  }

}
