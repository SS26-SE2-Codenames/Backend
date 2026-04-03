package com.codenames.codenames_backend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

  private final LobbyService lobbyService;
  private final ObjectMapper mapper = new ObjectMapper();

  public GameWebSocketHandler(LobbyService lobbyService) {
    this.lobbyService = lobbyService;
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    JsonNode json = mapper.readTree(message.getPayload());
    String type = json.get("type").asText();

    if (type.equals("JOIN")) {
      String name = json.get("name").asText();
      String code = json.get("code").asText();

      Player player = new Player(name, session);

      lobbyService.addPlayer(code, player);
      broadcastPlayerList(code);
    }
  }

  private void broadcastPlayerList(String code) throws Exception {

    var players = lobbyService.getPlayers(code);

    var names = players.stream().map(Player::getUsername).toList();

    String response =
        mapper.writeValueAsString(java.util.Map.of("type", "PLAYER_LIST", "players", names));

    for (Player p : players) {
      p.getSession().sendMessage(new TextMessage(response));
    }
  }
}
