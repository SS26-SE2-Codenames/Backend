package com.codenames.codenames_backend.lobby.controller;

import com.codenames.codenames_backend.lobby.dto.LobbyResponse;
import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.http.ResponseEntity;
import com.codenames.codenames_backend.lobby.dto.PositionSelectMessage;
import org.springframework.web.bind.annotation.*;

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
                    .body(new LobbyResponse("Error while creating lobby.", ""));
        } else {
            return ResponseEntity.ok(new LobbyResponse("Successfully created Lobby.", lobbyCode));
        }
    }


  /**
   * Handles a request to join an existing lobby.
   *
   * @param username the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return a response indicating whether the join was successful
   */
  @PostMapping("/join")
  public ResponseEntity<LobbyResponse> joinLobby(
      @RequestParam String username, @RequestParam String lobbyCode) {
    boolean joined = service.joinLobby(username, lobbyCode);
    if (joined) {
      return ResponseEntity.ok(new LobbyResponse("Joined Lobby successfully.", lobbyCode));
    } else {
      return ResponseEntity.badRequest()
          .body(new LobbyResponse("Could not find lobby.", lobbyCode));
    }
  }

  /**
   * Handles a request to leave a lobby.
   *
   * @param username the username of the player
   * @param lobbyCode the lobby code identifying the lobby
   * @return a response indicating whether the operation was successful
   */
  @PostMapping("/leave")
  public ResponseEntity<LobbyResponse> leaveLobby(
      @RequestParam String username, @RequestParam String lobbyCode) {
    boolean left = service.leaveLobby(username, lobbyCode);
    if (left) {
      return ResponseEntity.ok(new LobbyResponse("Left lobby successfully.", lobbyCode));
    } else {
      return ResponseEntity.badRequest()
          .body(new LobbyResponse("Could not find lobby.", lobbyCode));
    }
  }

    @PostMapping("/select-position")
    public ResponseEntity<LobbyResponse> selectPosition(
            @RequestBody PositionSelectMessage request
    ) {
        boolean updated = service.selectPosition(
                request.getUsername(),
                request.getLobbyCode(),
                request.getTeam(),
                request.getRole()
        );

        if (updated) {
            return ResponseEntity.ok(
                    new LobbyResponse("Position selected successfully.", request.getLobbyCode())
            );
        } else {
            return ResponseEntity.badRequest().body(
                    new LobbyResponse("Could not assign selected team/role.", request.getLobbyCode())
            );
        }
    }
}
