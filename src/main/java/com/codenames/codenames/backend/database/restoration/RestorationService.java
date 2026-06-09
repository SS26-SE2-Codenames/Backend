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
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/** Service class responsible for re-creating all lobbies and game managers. */
@Service
public class RestorationService {
  private final RestorationMapper restorationMapper;
  private final LobbyRepository lobbyRepository;

  private final LobbyService lobbyService;
  private final GameService gameService;
  private final GameManagerFactory gameManagerFactory;

  private final PlatformTransactionManager transactionManager;

  /**
   * Constructor for the service class.
   *
   * @param restorationMapper the helper class used to create Lobby and GameStateDto
   * @param lobbyRepository the lobby entry from the database
   * @param lobbyService service class responsible for adding a lobby to a list of active lobbies
   * @param gameService service class responsible for binding a gameManager to a lobby
   * @param gameManagerFactory factory class responsible for creating a gameManager
   */
  public RestorationService(
      RestorationMapper restorationMapper,
      LobbyRepository lobbyRepository,
      LobbyService lobbyService,
      GameService gameService,
      GameManagerFactory gameManagerFactory,
      PlatformTransactionManager transactionManager) {
    this.restorationMapper = restorationMapper;
    this.lobbyRepository = lobbyRepository;
    this.lobbyService = lobbyService;
    this.gameService = gameService;
    this.gameManagerFactory = gameManagerFactory;
    this.transactionManager = transactionManager;
  }

  /** Using PostConstruct we automatically call this method when the container restarts. */
  // @PostConstruct methods technically gets called right after the beans are initialized
  // Turns out you cannot have PostConstruct and Transactional annotation together
  // Solution is to use Transaction Template --> Solution found on StackOverflow
  @PostConstruct
  public void restoreOnContainerStart() {

    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setReadOnly(true);

    transactionTemplate.execute(
        new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            for (LobbyEntity lobbyEntity : lobbyRepository.findAll()) {
              String lobbyCode = lobbyEntity.getLobbyCode();

              Lobby lobbyObj = restorationMapper.mapToLobby(lobbyEntity);
              lobbyService.restoreLobby(lobbyCode, lobbyObj);

              GameStateDto gameStateDtoObj = restorationMapper.mapToGameStateDto(lobbyEntity);
              GameManager gameManagerObj = gameManagerFactory.createFromSnapshot(gameStateDtoObj);
              gameService.restoreGameManager(lobbyCode, gameManagerObj);
            }
          }
        });
  }
}
