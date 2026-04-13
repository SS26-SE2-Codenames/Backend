package com.codenames.codenames_backend.websocket;

import com.codenames.codenames_backend.lobby.services.LobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController {

    private final LobbyService lobbyService;

    public GameController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/join")
    @SendTo("/topic/lobby")
    public List<String> join(JoinMessage message) {

        lobbyService.joinLobby(message.getName(), message.getCode());

        return lobbyService.getPlayers(message.getCode())
                .stream()
                .map(p -> p.getUsername())
                .toList();
    }

}
