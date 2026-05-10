package com.codenames.codenames.backend.game.controller;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.game.dto.ClueMessage;
import com.codenames.codenames.backend.game.dto.RevealCardMessage;
import com.codenames.codenames.backend.game.dto.StartGameMessage;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.playingfield.CardGenerator;
import com.codenames.codenames.backend.playingfield.GameManager;
import com.codenames.codenames.backend.utility.Team;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class GameSocketController {

  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;
  private final CardGenerator cardGenerator;
  private final ClueValidationService clueValidationService;

  private final Map<String, GameManager> gameSessions = new ConcurrentHashMap<>();

  public GameSocketController(
      LobbyService lobbyService,
      SimpMessagingTemplate messagingTemplate,
      CardGenerator cardGenerator,
      ClueValidationService clueValidationService) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
    this.cardGenerator = cardGenerator;
    this.clueValidationService = clueValidationService;
  }

  @MessageMapping("/start-game")
  public void startGame(StartGameMessage message) {

    Team startingTeam = lobbyService.decideStartingTeam(message.getLobbyCode());

    GameManager gameManager = new GameManager(startingTeam, cardGenerator, clueValidationService);

    gameSessions.put(message.getLobbyCode(), gameManager);

    messagingTemplate.convertAndSend(
        "/topic/game/" + message.getLobbyCode(), gameManager.getCardList());
  }

  @MessageMapping("/reveal-card")
  public void revealCard(RevealCardMessage message) {

    GameManager gameManager = gameSessions.get(message.getLobbyCode());

    if (gameManager == null) {
      return;
    }

    gameManager.flipCard(message.getPosition(), message.getCurrentTurn());

    messagingTemplate.convertAndSend(
        "/topic/game/" + message.getLobbyCode(), gameManager.getCardList());
  }

  @MessageMapping("/submit-clue")
  public void submitClue(ClueMessage message) {

    GameManager gameManager = gameSessions.get(message.getLobbyCode());

    if (gameManager == null) {
      return;
    }

    Clue clue = new Clue(message.getWord(), message.getGuessAmount());

    gameManager.submitClue(clue);

    messagingTemplate.convertAndSend("/topic/game/" + message.getLobbyCode(), gameManager);
  }
}
