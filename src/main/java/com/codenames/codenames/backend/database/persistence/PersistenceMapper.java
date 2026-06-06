package com.codenames.codenames.backend.database.persistence;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.game.domain.Card;

import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Helper class that is called by the PersistenceService to map all the tables when a snapshot is
 * created.
 */
@Component
public class PersistenceMapper {
  // Should be possible to just call save on Lobby object due to cascade. Will find out in the
  // future when this feature/ ticket is finished.

  /**
   * This method creates and returns a lobby entity that is to be stored into the DB.
   *
   * @param lobbyCode the code to act as PK and FK for other tables
   * @param gameManager the gameManager of an active game to derive information for other tables
   * @param players the list of players in an active game
   * @return the lobby entity to be stored into dp
   */
  public LobbyEntity mapAggregateParentLobbyEntity(
      String lobbyCode, GameManager gameManager, List<PlayerDto> players) {

    LobbyEntity lobbyEntity = new LobbyEntity();
    lobbyEntity.setLobbyCode(lobbyCode);

    List<Card> cardList = gameManager.getCardList();

    lobbyEntity.setGameStateEntity(mapGameState(lobbyEntity, lobbyCode, gameManager));
    lobbyEntity.setCardEntities(mapCard(lobbyEntity, lobbyCode, cardList));
    lobbyEntity.setPlayerEntities(mapPlayer(lobbyEntity, lobbyCode, players));

    return lobbyEntity;
  }

  private List<PlayerEntity> mapPlayer(
      LobbyEntity lobbyEntity, String lobbyCode, List<PlayerDto> players) {
    List<PlayerEntity> playerList = new ArrayList<>();

    for (int i = 0; i < players.size(); i++) {
      PlayerEntity playerEntity = new PlayerEntity();
      playerEntity.setLobbyEntity(lobbyEntity);
      playerEntity.setUsername(players.get(i).username());
      playerEntity.setIsHost(players.get(i).isHost());
      playerEntity.setTeam(players.get(i).team().name());
      playerEntity.setRole(players.get(i).role().name());
      playerList.add(playerEntity);
    }
    return playerList;
  }

  // Since we are using ORM we always need to map the object as well.

  private GameStateEntity mapGameState(
      LobbyEntity lobbyEntity, String lobbyCode, GameManager gameManager) {
    GameStateEntity gameStateEntity = new GameStateEntity();
    gameStateEntity.setLobbyEntity(lobbyEntity);
    // Since we do not have autogenerating PK we need to manually map.
    gameStateEntity.setLobbyCode(lobbyCode);
    gameStateEntity.setCurrentTurn(gameManager.getCurrentTurn().name());
    gameStateEntity.setCurrentPhase(gameManager.getCurrentPhase().name());
    gameStateEntity.setClueWord(gameManager.getCurrentClueWord());
    gameStateEntity.setClueGuessAmount(gameStateEntity.getClueGuessAmount());
    gameStateEntity.setRemainingGuesses(gameStateEntity.getRemainingGuesses());
    return gameStateEntity;
  }

  private List<CardEntity> mapCard(LobbyEntity lobbyEntity, String lobbyCode, List<Card> cards) {
    List<CardEntity> cardList = new ArrayList<>();
    for (int i = 0; i < cards.size(); i++) {
      CardEntity cardEntity = new CardEntity();
      cardEntity.setLobbyEntity(lobbyEntity);
      cardEntity.setPosition(i);
      cardEntity.setWord(cards.get(i).getWord());
      cardEntity.setColor(cards.get(i).getColor().name());
      cardEntity.setIsGuessed(cards.get(i).isGuessed());
      cardList.add(cardEntity);
    }
    return cardList;
  }
}
