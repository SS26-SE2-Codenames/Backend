package com.codenames.codenames.backend.lobby.controller;

import com.codenames.codenames.backend.lobby.dto.LobbyResponse;
import com.codenames.codenames.backend.lobby.dto.PlayerDto;
import com.codenames.codenames.backend.lobby.services.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  /**
   * Creates a new {@code LobbyController}.
   *
   * @param service the lobby service used to handle business logic
   */
  public LobbyController(LobbyService service) {
    this.service = service;
  }

  /**
   * Handles a request to create a new lobby.
   *
   * @param username the username of the requesting user
   * @return a response containing the result and the generated lobby code
   */

  @PostMapping("/create")
  public ResponseEntity<LobbyResponse> createLobby(@RequestParam String username) {
    String lobbyCode = service.createLobby(username);
    if (lobbyCode == null || lobbyCode.isBlank()) {
      return ResponseEntity.internalServerError()
          .body(new LobbyResponse("Error while creating lobby.", "", null));
    } else {
      List<PlayerDto> players = service.getPlayersDto(lobbyCode);
      return ResponseEntity.ok(new LobbyResponse("Successfully created Lobby.", lobbyCode, players));
    }
  }

  /**
   * Handles a request to join an existing lobby.
   *
   * @param username  the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return a response indicating whether the join was successful
   */
  @PostMapping("/join")
  public ResponseEntity<LobbyResponse> joinLobby(
      @RequestParam String username, @RequestParam String lobbyCode) {
    boolean joined = service.joinLobby(username, lobbyCode);
    if (joined) {
      return ResponseEntity.ok(new LobbyResponse("Joined Lobby successfully.", lobbyCode, service.getPlayersDto(lobbyCode)));
    } else {
      return ResponseEntity.badRequest()
          .body(new LobbyResponse("Could not find lobby.", lobbyCode, null));
    }
  }

  /**
   * Handles a request to leave a lobby.
   *
   * @param username  the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return a response indicating whether the operation was successful
   */
  @PostMapping("/{lobbyCode}/leave")
  public ResponseEntity<LobbyResponse> leaveLobby(
          @PathVariable String lobbyCode,
          @RequestParam String username) {
    boolean left = service.leaveLobby(username, lobbyCode);
    System.out.println("LobbyCode: " + lobbyCode);
    System.out.println("Username: " + username);
    if (left) {
      ResponseEntity<LobbyResponse> response = ResponseEntity.ok(new LobbyResponse("Left lobby successfully.", lobbyCode, service.getPlayersDto(lobbyCode)));
      service.checkLobbyStillHasPlayers(lobbyCode);
      return response;
    } else {
      return ResponseEntity.badRequest()
          .body(new LobbyResponse("Could not find lobby.", lobbyCode, null));
    }
  }

  @GetMapping("/{lobbyCode}")
  public ResponseEntity<LobbyResponse> getLobbyInfo(
          @PathVariable String lobbyCode
  ) {
    List<PlayerDto> players = service.getPlayersDto(lobbyCode);
    if (players != null) {
      return ResponseEntity.ok(new LobbyResponse("Lobby info retrieved successfully.", lobbyCode, players));
    } else {
      return ResponseEntity.badRequest()
          .body(new LobbyResponse("Could not find lobby.", lobbyCode, null));
    }
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
      return ResponseEntity.ok(
          new LobbyResponse("Position selected successfully.", lobbyCode, service.getPlayersDto(lobbyCode))
      );
    } else {
      return ResponseEntity.badRequest().body(
          new LobbyResponse("Could not assign selected team/role.", lobbyCode, service.getPlayersDto(lobbyCode))
      );
    }
  }
}
