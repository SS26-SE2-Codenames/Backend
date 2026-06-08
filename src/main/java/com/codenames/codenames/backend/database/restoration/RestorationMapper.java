package com.codenames.codenames.backend.database.restoration;

import com.codenames.codenames.backend.database.entity.GameStateEntity;
import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.game.api.dto.CardDto;
import com.codenames.codenames.backend.game.api.dto.ClueDto;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.domain.Card;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RestorationMapper {

  public Lobby mapToLobby(LobbyEntity lobbyEntity) {
    Lobby lobby = new Lobby("sd", "d");
    GameStateDto gameStateDto = mapToGameStateDto(lobbyEntity);
    return lobby;
  }

  private GameStateDto mapToGameStateDto(LobbyEntity lobbyEntity) {
    GameStateEntity gameStateEntity = lobbyEntity.getGameStateEntity();
    if (gameStateEntity == null) {
      throw new RuntimeException("Game state entity is null");
    }

    Team winner = null;
    // valueOf takes string and returns enum of that string
    Team currentTeam = Team.valueOf(gameStateEntity.getCurrentTurn());
    Role currentPhase = Role.valueOf(gameStateEntity.getCurrentPhase());
    ClueDto clueDto = null;
    if(gameStateEntity.getClueWord() != null) {
      clueDto = new ClueDto(gameStateEntity.getClueWord(), gameStateEntity.getClueGuessAmount());
    }
    List<CardDto> cardList = new ArrayList<>(25);

    return new GameStateDto(winner, currentTeam, currentPhase, clueDto, cardList);

  }

  private List<CardDto> mapToCardDto(LobbyEntity lobbyEntity) {

  }
}
