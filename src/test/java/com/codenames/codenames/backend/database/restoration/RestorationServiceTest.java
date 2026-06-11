package com.codenames.codenames.backend.database.restoration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.database.entity.LobbyEntity;
import com.codenames.codenames.backend.database.repository.LobbyRepository;
import com.codenames.codenames.backend.game.api.dto.CardDto;
import com.codenames.codenames.backend.game.api.dto.ClueDto;
import com.codenames.codenames.backend.game.api.dto.GameStateDto;
import com.codenames.codenames.backend.game.application.GameService;
import com.codenames.codenames.backend.game.domain.GameManager;
import com.codenames.codenames.backend.game.domain.GameManagerFactory;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.lobby.domain.Lobby;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

class RestorationServiceTest {
  private RestorationService restorationService;
  private RestorationMapper restorationMapper;
  private GameManagerFactory gameManagerFactory;
  private LobbyService lobbyService;
  private LobbyRepository lobbyRepository;
  private GameService gameService;
  private TransactionStatus transactionStatus;
  private PlatformTransactionManager transactionManager;
  private GameStateDto gameStateDto;
  private List<CardDto> cardDtoList;
  private Lobby mockLobby;
  private GameManager mockGameManager;

  private static final Team redTeam = Team.RED;
  private static final Role spymaster = Role.SPYMASTER;

  @BeforeEach
  void setUp() {
    restorationMapper = mock(RestorationMapper.class);
    lobbyService = mock(LobbyService.class);
    lobbyRepository = mock(LobbyRepository.class);
    gameManagerFactory = mock(GameManagerFactory.class);
    gameService = mock(GameService.class);
    transactionManager = mock(PlatformTransactionManager.class);
    transactionStatus = mock(TransactionStatus.class);


    mockLobby = mock(Lobby.class);
    mockGameManager = mock(GameManager.class);

    cardDtoList = List.of(new CardDto("TEST", null, false));
    gameStateDto =
        new GameStateDto(redTeam, redTeam, spymaster, new ClueDto("Test", 1), cardDtoList);

    when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

    restorationService =
        new RestorationService(
            restorationMapper,
            lobbyRepository,
            lobbyService,
            gameService,
            gameManagerFactory,
            transactionManager);
  }

  @Test
  void testRestoreOnContainerStart() {
    LobbyEntity lobbyEntity = new LobbyEntity();
    lobbyEntity.setLobbyCode("ABCDE");
    when(lobbyRepository.findAll()).thenReturn(List.of(lobbyEntity));

    when(restorationMapper.mapToGameStateDto(lobbyEntity)).thenReturn(gameStateDto);
    when(restorationMapper.mapToLobby(lobbyEntity)).thenReturn(mockLobby);
    when(gameManagerFactory.createFromSnapshot(gameStateDto)).thenReturn(mockGameManager);

    restorationService.restoreOnContainerStart();

    verify(lobbyService, times(1)).restoreLobby("ABCDE", mockLobby);
    verify(gameService, times(1)).restoreGameManager("ABCDE", mockGameManager);
  }
}
