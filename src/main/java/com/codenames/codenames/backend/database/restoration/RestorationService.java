package com.codenames.codenames.backend.database.restoration;

import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.repository.LobbyRepository;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.game.domain.GameManagerFactory;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class RestorationService {
  private final RestorationMapper restorationMapper;
  private final LobbyRepository lobbyRepository;

  private final LobbyService lobbyService;
  private final GameService gameService;
  private final GameManagerFactory gameManagerFactory;

  public RestorationService(RestorationMapper restorationMapper, LobbyRepository lobbyRepository,
      LobbyService lobbyService, GameService gameService, GameManagerFactory gameManagerFactory) {
    this.restorationMapper = restorationMapper;
    this.lobbyRepository = lobbyRepository;
    this.lobbyService = lobbyService;
    this.gameService = gameService;
    this.gameManagerFactory = gameManagerFactory;
  }

  @PostConstruct
  public void restoreOnContainerStart() {
    for (LobbyEntity lobbyEntity : lobbyRepository.findAll()) {
      String lobbyCode = lobbyEntity.getLobbyCode();

      Lobby lobbyObj = restorationMapper.mapToLobby(lobbyEntity);
      lobbyService.restoreLobby(lobbyCode, lobbyObj);

      GameStateDto gameStateDtoObj = restorationMapper.mapToGameStateDto(lobbyEntity);
      GameManager gameManagerObj = gameManagerFactory.createFromSnapshot(gameStateDtoObj);
      gameService.restoreGameManager(lobbyCode, gameManagerObj);
    }
  }
}
