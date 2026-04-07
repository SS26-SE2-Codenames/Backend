package com.codenames.codenames_backend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * WebSocket handler responsible for processing incoming messages and managing player interactions
 * in lobbies.
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
  private static final String TYPE = "type";
  private static final String TYPE_JOIN = "JOIN";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_CODE = "code";
  private static final String RESPONSE_TYPE = "PLAYER_LIST";
  private static final String FIELD_PLAYERS = "players";

  private final LobbyService lobbyService;
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Creates a new WebSocket handler.
   *
   * @param lobbyService the lobby service used for managing players
   */
  public GameWebSocketHandler(LobbyService lobbyService) {
    this.lobbyService = lobbyService;
  }

  /**
   * Handles incoming WebSocket text messages.
   *
   * @param session the WebSocket session of the client
   * @param message the incoming message
   * @throws IOException if message parsing or response sending fails
   */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message)
      throws IOException {
    JsonNode json = mapper.readTree(message.getPayload());

    JsonNode typeNode = json.get(TYPE);
    if (typeNode == null) {
      return;
    }

    String type = typeNode.asText();

    if (type.equals(TYPE_JOIN)) {
      handleJoin(json, session);
    }
  }

  /**
   * Handles a player joining a lobby.
   *
   * @param json the parsed JSON message
   * @param session the WebSocket session of the player
   * @throws IOException if broadcasting fails
   */
  private void handleJoin(JsonNode json, WebSocketSession session) throws IOException {

    JsonNode nameNode = json.get(FIELD_NAME);
    JsonNode codeNode = json.get(FIELD_CODE);

    if (nameNode == null || codeNode == null) {
      return;
    }
    String name = json.get(FIELD_NAME).asText();
    String code = json.get(FIELD_CODE).asText();

    Player player = new Player(name, session);
    lobbyService.addPlayer(code, player);

    broadcastPlayerList(code);
  }

  /**
   * Sends the updated player list to all players in a lobby.
   *
   * @param code the lobby code
   * @throws IOException if sending messages fails
   */
  private void broadcastPlayerList(String code) throws IOException {

    var players = lobbyService.getPlayers(code);

    var names = players.stream().map(Player::getUsername).toList();

    String response =
        mapper.writeValueAsString(java.util.Map.of(TYPE, RESPONSE_TYPE, FIELD_PLAYERS, names));

    for (Player p : players) {
      p.getSession().sendMessage(new TextMessage(response));
    }
  }
}
