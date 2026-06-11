package com.codenames.codenames.backend.database.restoration;

import com.codenames.codenames.backend.database.entity.CardEntity;
import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.entity.PlayerEntity;
import com.codenames.codenames.backend.game.api.dto.CardDto;
import com.codenames.codenames.backend.game.api.dto.ClueDto;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.domain.Color;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Helper class responsible for creating Lobby and GameStateDto, used by RestorationService.
 */
@Component
public class RestorationMapper {

  /**
   * Exposed method to generate a Lobby object.
   *
   * @param lobbyEntity the lobby entry from the database
   * @return a Lobby object to be used by RestorationService
   */
  public Lobby mapToLobby(LobbyEntity lobbyEntity) {
    String lobbyCode = lobbyEntity.getLobbyCode();
    List<PlayerDto> playerDtoList = mapToPlayerDto(lobbyEntity);
    return buildLobby(lobbyCode, playerDtoList);
  }

  /**
   * Exposed method to generate a GameStateDto object.
   *
   * @param lobbyEntity the lobby entry from the database
   * @return a Lobby object to be used by RestorationService
   */
  public GameStateDto mapToGameStateDto(LobbyEntity lobbyEntity) {
    GameStateEntity gameStateEntity = lobbyEntity.getGameStateEntity();
    if (gameStateEntity == null) {
      throw new IllegalStateException("Game state entity is null");
    }

    Team winner = null;
    // valueOf takes string and returns enum of that string
    Team currentTeam = Team.valueOf(gameStateEntity.getCurrentTurn());
    Role currentPhase = Role.valueOf(gameStateEntity.getCurrentPhase());
    ClueDto clueDto = null;
    if (gameStateEntity.getClueWord() != null) {
      clueDto = new ClueDto(gameStateEntity.getClueWord(), gameStateEntity.getClueGuessAmount());
    }
    List<CardDto> cardList = mapToCardDto(lobbyEntity);
    return new GameStateDto(winner, currentTeam, currentPhase, clueDto, cardList,
            gameStateEntity.getRemainingGuesses());
  }

  private List<CardDto> mapToCardDto(LobbyEntity lobbyEntity) {
    List<CardDto> cardList = new ArrayList<>(25);

    for (CardEntity currentCardEntity : lobbyEntity.getCardEntities()) {
      CardDto cardDto =
          new CardDto(
              currentCardEntity.getWord(),
              Color.valueOf(currentCardEntity.getColor()),
              currentCardEntity.getIsGuessed());
      cardList.add(cardDto);
    }
    return cardList;
  }

  private List<PlayerDto> mapToPlayerDto(LobbyEntity lobbyEntity) {
    List<PlayerDto> playerList = new ArrayList<>();

    for (int i = 0; i < lobbyEntity.getPlayerEntities().size(); i++) {
      PlayerEntity playerEntity = lobbyEntity.getPlayerEntities().get(i);
      Team team = null;
      if (playerEntity.getTeam() != null) {
        team = Team.valueOf(playerEntity.getTeam());
      }
      Role role = null;
      if (playerEntity.getRole() != null) {
        role = Role.valueOf(playerEntity.getRole());
      }
      PlayerDto playerDto =
          new PlayerDto(
              playerEntity.getUsername(),
              team,
              role,
              playerEntity.getIsHost());
      playerList.add(playerDto);
    }
    return playerList;
  }

  private Lobby buildLobby(String lobbyCode, List<PlayerDto> players) {
    String hostUsername = findHostUsername(players);

    Lobby lobby = new Lobby(lobbyCode, hostUsername);

    for (PlayerDto player : players) {
      if (!player.username().equals(hostUsername)) {
        lobby.addPlayer(player.username(), player.isHost());
      }
      if (player.team() != null) {
        lobby.setPlayerTeam(player.username(), player.team());
      }
      if (player.role() != null) {
        lobby.setPlayerRole(player.username(), player.role());
      }
    }

    return lobby;
  }

  private static String findHostUsername(List<PlayerDto> players) {
    String hostUsername = "";
    for (PlayerDto playerDto : players) {
      if (playerDto.isHost()) {
        hostUsername = playerDto.username();
      }
    }
    return hostUsername;
  }
}
