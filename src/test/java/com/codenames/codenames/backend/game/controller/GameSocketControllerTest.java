package com.codenames.codenames.backend.game.controller;

import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.game.dto.ClueMessage;
import com.codenames.codenames.backend.game.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.dto.StartGameMessage;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.Card;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import com.codenames.codenames.backend.utility.Color;
import com.codenames.codenames.backend.utility.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameSocketControllerTest {
  @Mock private LobbyService lobbyService;

  @Mock private SimpMessagingTemplate messagingTemplate;

  @Mock private CardGenerator cardGenerator;

  @Mock private ClueValidationService clueValidationService;

  private GameSocketController controller;

  @BeforeEach
  void setUp() {
    controller =
        new GameSocketController(
            lobbyService, messagingTemplate, cardGenerator, clueValidationService);
  }

  @Test
  void startGameShouldBroadcastBoard() {

    when(lobbyService.decideStartingTeam("ABCDE")).thenReturn(Team.RED);

    StartGameMessage message = new StartGameMessage();

    message.setLobbyCode("ABCDE");

    controller.startGame(message);

    verify(messagingTemplate).convertAndSend(eq("/topic/game/ABCDE"), any(Object.class));
  }

  @Test
  void revealCardShouldBroadcastBoardUpdate() {

    when(lobbyService.decideStartingTeam("ABCDE")).thenReturn(Team.RED);

    when(cardGenerator.generateCards(anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(List.of(new Card("Test", Color.RED)));

    StartGameMessage startMessage = new StartGameMessage();

    startMessage.setLobbyCode("ABCDE");

    when(clueValidationService.validateWord(any(), anyString())).thenReturn(true);

    controller.startGame(startMessage);

    ClueMessage clueMessage = new ClueMessage();

    clueMessage.setLobbyCode("ABCDE");
    clueMessage.setWord("animal");
    clueMessage.setGuessAmount(1);

    controller.submitClue(clueMessage);

    RevealCardMessage revealMessage = new RevealCardMessage();

    revealMessage.setLobbyCode("ABCDE");
    revealMessage.setPosition(0);
    revealMessage.setCurrentTurn(Color.RED);

    controller.revealCard(revealMessage);

    verify(messagingTemplate, times(3)).convertAndSend(eq("/topic/game/ABCDE"), any(Object.class));
  }

  @Test
  void revealCardShouldReturnWhenGameSessionMissing() {

    RevealCardMessage message = new RevealCardMessage();

    message.setLobbyCode("UNKNOWN");

    controller.revealCard(message);

    verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void submitClueShouldBroadcastGameUpdate() {

    when(lobbyService.decideStartingTeam("ABCDE")).thenReturn(Team.RED);

    StartGameMessage startMessage = new StartGameMessage();

    startMessage.setLobbyCode("ABCDE");

    controller.startGame(startMessage);

    ClueMessage clueMessage = new ClueMessage();

    clueMessage.setLobbyCode("ABCDE");
    clueMessage.setWord("animal");
    clueMessage.setGuessAmount(2);

    when(clueValidationService.validateWord(any(), anyString())).thenReturn(true);

    controller.submitClue(clueMessage);

    verify(messagingTemplate, times(2)).convertAndSend(eq("/topic/game/ABCDE"), any(Object.class));
  }

  @Test
  void submitClueShouldReturnWhenGameSessionMissing() {

    ClueMessage clueMessage = new ClueMessage();

    clueMessage.setLobbyCode("UNKNOWN");
    clueMessage.setWord("animal");
    clueMessage.setGuessAmount(2);

    controller.submitClue(clueMessage);

    verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
  }
}
