package com.codenames.codenames.backend.database.persistence;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.game.domain.Card;

import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import java.util.List;

public class PersistenceMapper {
  // Should be possible to just call save on Lobby object due to cascade. Will find out in the
  // future when this feature/ ticket is finished.
  public LobbyEntity mapLobbyEntity(String lobbyCode, GameManager gameManager, List<PlayerDto> players) {
    LobbyEntity lobby = new LobbyEntity();
    lobby.setLobbyCode(lobbyCode);

    return lobby;
  }
  private GameStateEntity mapGameState(GameManager gameManager) {
  }

  private CardEntity mapCard(List<Card> cards) {
  }
  private PlayerEntity mapPlayer(List<PlayerDto> players) {

  }
}
