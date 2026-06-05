package com.codenames.codenames.backend.database.persistence;

import com.codenames.codenames.backend.database.Card;
import com.codenames.codenames.backend.game.domain.Card;
import com.codenames.codenames.backend.database.GameState;
import com.codenames.codenames.backend.database.Lobby;
import com.codenames.codenames.backend.database.Player;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import java.util.List;

public class PersistenceMapper {
  // Should be possible to just call save on Lobby object due to cascade. Will find out in the
  // future when this feature/ ticket is finished.
  public Lobby mapLobbyEntity(String lobbyCode, GameManager gameManager, List<PlayerDto> players) {
    Lobby lobby = new Lobby();
    lobby.setLobbyCode(lobbyCode);

    return lobby;
  }
  private GameState mapGameState(GameState gameState) {
    return gameState;
  }

  private Card mapCard(List<Card> cards) {
    return card;
  }
  private Player mapPlayer(List<PlayerDto> players) {

  }
}
