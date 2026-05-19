package com.codenames.codenames.backend.lobby.controller;

import com.codenames.codenames.backend.lobby.dto.LobbyResponse;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import com.codenames.codenames.backend.recovery.SystemStatePersistenceService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling lobby management operations.
 *
 * <p>Provides endpoints for creating, joining, and leaving lobbies. Delegates business logic to
 * {@link LobbyService}.
 */

@RestController
@RequestMapping("/lobby")
public class LobbyController {

  private final LobbyService service;
  private final SystemStatePersistenceService persistenceService;
  private static final String LOBBY_NOT_FOUND = "Could not find lobby.";

  /**
   * Creates a new {@code LobbyController}.
   *
   * @param service the lobby service used to handle business logic
   * @param persistenceService service used to persist current backend state
   */
  public LobbyController(LobbyService service, SystemStatePersistenceService persistenceService) {
    this.service = service;
    this.persistenceService = persistenceService;
  }

  /**
   * Handles a request to create a new lobby.
   *
   * @param username the username of the requesting user
   * @return a response containing the result and the generated lobby code
   */

  @GetMapping("/create")
  public ResponseEntity<LobbyResponse> createLobby(@RequestParam String username) {
    String lobbyCode = service.createLobby(username);
    if (lobbyCode == null || lobbyCode.isBlank()) {
      return ResponseEntity.internalServerError()
              .body(new LobbyResponse("Error while creating lobby.", "", null, false));
    } else {
      persistenceService.persistCurrentState();
      List<PlayerDto> players = service.getPlayersDto(lobbyCode);
      return ResponseEntity.ok(
              new LobbyResponse("Successfully created Lobby.", lobbyCode, players, false)
      );
    }
  }

  /**
   * Handles a request to join an existing lobby.
   *
   * @param username  the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return a response indicating whether the join was successful
   */
  @GetMapping("/{lobbyCode}/join")
  public ResponseEntity<LobbyResponse> joinLobby(
          @RequestParam String username, @PathVariable String lobbyCode) {
    boolean joined = service.joinLobby(username, lobbyCode);
    if (joined) {
      persistenceService.persistCurrentState();
      return ResponseEntity.ok(
              new LobbyResponse(
                      "Joined Lobby successfully.",
                      lobbyCode,
                      service.getPlayersDto(lobbyCode),
                      false
              )
      );
    } else {
      return ResponseEntity.badRequest()
              .body(new LobbyResponse(LOBBY_NOT_FOUND, lobbyCode, null, false));
    }
  }

  /**
   * Handles a request to leave a lobby.
   *
   * @param username  the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return a response indicating whether the operation was successful
   */
  @GetMapping("/{lobbyCode}/leave")
  public ResponseEntity<LobbyResponse> leaveLobby(
          @PathVariable String lobbyCode,
          @RequestParam String username) {
    boolean left = service.leaveLobby(username, lobbyCode);
    if (left) {
      service.checkLobbyStillHasPlayers(lobbyCode);
      persistenceService.persistCurrentState();
      ResponseEntity<LobbyResponse> response = ResponseEntity.ok(
              new LobbyResponse(
                      "Left lobby successfully.",
                      lobbyCode,
                      service.getPlayersDto(lobbyCode),
                      false
              )
      );
      return response;
    } else {
      return ResponseEntity.badRequest()
              .body(new LobbyResponse(LOBBY_NOT_FOUND, lobbyCode, null, false));
    }
  }

  /**
   * An endpoint for retrieving all lobby-specific info used during polling in lobby-state.
   *
   * @param lobbyCode unique lobby code
   * @return a response entity with the http code 200 for ok and
   *         400 for bad request, if an error occurred
   */

  @GetMapping("/{lobbyCode}")
  public ResponseEntity<LobbyResponse> getLobbyInfo(
          @PathVariable String lobbyCode
  ) {
    List<PlayerDto> players = service.getPlayersDto(lobbyCode);
    boolean isStarted = service.getIsStarted(lobbyCode);
    return ResponseEntity.ok(
            new LobbyResponse("Lobby info retrieved successfully.", lobbyCode, players, isStarted)
    );
  }

  /**
   * Handles a request to select a team and role for a player.
   *
   * @param request the position selection request containing username, lobby code, team, and role
   * @return a response indicating whether the selection was successful
   */
  @PostMapping("/{lobbyCode}/select-position")
  public ResponseEntity<LobbyResponse> selectPosition(
          @PathVariable String lobbyCode, @RequestBody PlayerDto request
  ) {
    boolean updated = service.selectPosition(
            request.username(),
            lobbyCode,
            request.team(),
            request.role()
    );

    if (updated) {
      persistenceService.persistCurrentState();
      return ResponseEntity.ok(
              new LobbyResponse(
                      "Position selected successfully.",
                      lobbyCode,
                      service.getPlayersDto(lobbyCode),
                      false
              )
      );
    } else {
      return ResponseEntity.badRequest().body(
              new LobbyResponse(
                      "Could not assign selected team/role.",
                      lobbyCode,
                      service.getPlayersDto(lobbyCode),
                      false
              )
      );
    }
  }

  /**
   * Endpoint for starting a game, this is the last http-request only the host can make.
   *
   * @param lobbyCode the unique lobby code
   * @param username the name of the requesting user
   * @return a response entity of a lobby response, with isStarted @code true or @code false,
   *      whether the starting was successful or not
   */

  @GetMapping("/{lobbyCode}/start-game")
  public ResponseEntity<LobbyResponse> startGame(
          @PathVariable String lobbyCode, @RequestParam String username
  ) {
    boolean isStarted = service.startGame(lobbyCode, username);

    if (isStarted) {
      persistenceService.persistCurrentState();
      return ResponseEntity.ok(
              new LobbyResponse(
                      "Game is starting now.",
                      lobbyCode,
                      service.getPlayersDto(lobbyCode),
                      true
              )
      );
    }
    return ResponseEntity.badRequest().body(
            new LobbyResponse(
                    "Could not start the game.",
                    lobbyCode,
                    service.getPlayersDto(lobbyCode),
                    false
            )
    );
  }
}
