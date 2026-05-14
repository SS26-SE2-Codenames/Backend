package com.codenames.codenames.backend.playingfield;

import com.codenames.codenames.backend.clue.Clue;
import com.codenames.codenames.backend.clue.ClueValidationService;
import com.codenames.codenames.backend.utility.Team;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final Map<String, GameManager> games = new ConcurrentHashMap<>();
  private final CardGenerator cardGenerator;
  private final ClueValidationService clueValidationService;

  public GameService(CardGenerator cardGenerator, ClueValidationService clueValidationService) {
    this.cardGenerator = cardGenerator;
    this.clueValidationService = clueValidationService;
  }

  public void createGameManager(String lobbyCode, Team startingTeam) {
    games.computeIfAbsent(
        lobbyCode, game -> new GameManager(startingTeam, cardGenerator, clueValidationService));
  }

  public void removeGame(String lobbyCode) {
    games.remove(lobbyCode);
  }

  public GameManager getGame(String lobbyCode) {
    if (games.get(lobbyCode) == null) {
      throw new IllegalStateException("GameManager does not exist for lobby: " + lobbyCode);
    }
    return games.get(lobbyCode);
  }

  public void submitClue(String lobbyCode, Clue clue, Team callingTeam) {
    GameManager gm = getGame(lobbyCode);
    gm.submitClue(clue, callingTeam);
    gm.advanceTurn();
  }

  public void flipCard(String lobbyCode, int position, Team callingTeam) {
    GameManager gm = getGame(lobbyCode);
    gm.flipCard(position, callingTeam);
  }

  public void passTurn(String lobbyCode, Team callingTeam) {
    GameManager gm = getGame(lobbyCode);
    gm.passTurn(callingTeam);
  }
}
