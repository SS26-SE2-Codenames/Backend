package com.codenames.codenames_backend.lobby.controller;

import com.codenames.codenames_backend.lobby.dto.LobbyResponse;
import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.codenames.codenames_backend.lobby.Role;
import com.codenames.codenames_backend.lobby.Team;

@RestController
@RequestMapping("/lobby")
public class LobbyController {

    private final LobbyService service;

    public LobbyController(LobbyService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<LobbyResponse> createLobby(@RequestParam String username) {
        String lobbyCode = service.createLobby(username);
        if (lobbyCode.isBlank()) {
            return ResponseEntity.internalServerError().body(new LobbyResponse("Error while creating lobby.", ""));
        } else {
            return ResponseEntity.ok(new LobbyResponse("Successfully created Lobby.", lobbyCode));
        }
    }

    @PostMapping("/join")
    public ResponseEntity<LobbyResponse> joinLobby(@RequestParam String username, @RequestParam String lobbyCode) {
        boolean joined = service.joinLobby(username, lobbyCode);
        if (joined) {
            return ResponseEntity.ok(new LobbyResponse("Joined Lobby successfully.", lobbyCode));
        } else {
            return ResponseEntity.badRequest().body(new LobbyResponse("Could not find lobby.", lobbyCode));
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<LobbyResponse> leaveLobby(@RequestParam String username, @RequestParam String lobbyCode) {
        boolean left = service.leaveLobby(username, lobbyCode);
        if (left) {
            return ResponseEntity.ok(new LobbyResponse("Left lobby successfully.", lobbyCode));
        } else {
            return ResponseEntity.badRequest().body(new LobbyResponse("Could not find lobby.", lobbyCode));
        }
    }

    @PostMapping("/select-position")
    public ResponseEntity<LobbyResponse> selectPosition(
            @RequestParam String username,
            @RequestParam String lobbyCode,
            @RequestParam Team team,
            @RequestParam Role role
    ) {
        boolean updated = service.selectPosition(username, lobbyCode, team, role);

        if (updated) {
            return ResponseEntity.ok(
                    new LobbyResponse("Position selected successfully.", lobbyCode)
            );
        } else {
            return ResponseEntity.badRequest().body(
                    new LobbyResponse("Could not assign selected team/role.", lobbyCode)
            );
        }
    }
}
