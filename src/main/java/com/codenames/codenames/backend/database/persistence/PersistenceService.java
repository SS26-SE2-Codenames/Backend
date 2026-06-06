package com.codenames.codenames.backend.database.persistence;

import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.repository.LobbyRepository;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.lobby.api.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for storing a snapshot into the database. */
@Service
@Slf4j
@RequiredArgsConstructor
public class PersistenceService {
  private final LobbyService lobbyService;
  private final GameService gameService;
  private final PersistenceMapper persistenceMapper;
  private final LobbyRepository lobbyRepository;

  // according to spring docs I might need to configure it somewhere, will check later
  // default setting is that it triggers rollback on error, should act as transaction from DBMS

  /**
   * Method to create a snapshot and save it to the database.
   *
   * @param lobbyCode the code to identify a lobby
   */
  @Transactional
  public void saveSnapShot(String lobbyCode) {
    log.info("Saving snapshot of lobby: {}", lobbyCode);
    GameManager gameManager = gameService.getGameState(lobbyCode);
    List<PlayerDto> playerList = lobbyService.getPlayersDto(lobbyCode);
    LobbyEntity lobbySnapShot =
        persistenceMapper.mapAggregateParentLobbyEntity(lobbyCode, gameManager, playerList);
    lobbyRepository.save(lobbySnapShot);
  }
}
