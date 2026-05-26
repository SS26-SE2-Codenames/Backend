package com.codenames.codenames.backend.chat.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.chat.api.dto.ChatDto;
import com.codenames.codenames.backend.lobby.application.LobbyService;
import com.codenames.codenames.backend.chat.application.ChatService;
import com.codenames.codenames.backend.shared.websocket.SessionRegistry;
import com.codenames.codenames.backend.chat.api.dto.ChatMessageType;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

/** Unit test for ChatController. */
class ChatControllerTest {
  private ChatController chatController;
  private ChatService chatService;
  private SessionRegistry sessionRegistry;
  private LobbyService lobbyService;
  private SimpMessageHeaderAccessor headerAccessor;

  private final String sessionId = "session123";
  private final String lobbyId = "lobby123";
  private final String realUsername = "verifiedTest";
  private Team redTeam;
  private Team blueTeam;
  private ChatDto chatDto;

  @BeforeEach
  void setUp() {
    chatService = mock(ChatService.class);
    sessionRegistry = mock(SessionRegistry.class);
    lobbyService = mock(LobbyService.class);
    headerAccessor = mock(SimpMessageHeaderAccessor.class);

    chatController = new ChatController(chatService, lobbyService);

    chatDto = new ChatDto("TestName", "TestMessage", ChatMessageType.CHAT);

    when(headerAccessor.getSessionId()).thenReturn(sessionId);
    when(sessionRegistry.getUser(sessionId)).thenReturn(realUsername);
    when(sessionRegistry.getLobby(sessionId)).thenReturn(lobbyId);

    redTeam = Team.RED;
    blueTeam = Team.BLUE;
  }

  @Test
  void testSendLobbyMessage_valid() {
    chatController.sendLobbyMessage(lobbyId, chatDto);

    ChatDto verifiedChatDto = new ChatDto(chatDto.senderUsername(), chatDto.content(), chatDto.type());
    verify(chatService, times(1)).processMessage(lobbyId, "LOBBY", "", verifiedChatDto);
  }

  @Test
  void testSendTeamMessage_valid() {
    when(lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId)).thenReturn(redTeam);

    chatController.sendTeamMessage(lobbyId, redTeam, chatDto);

    ChatDto verifiedChatDto = new ChatDto(chatDto.senderUsername(), chatDto.content(), chatDto.type());
    verify(chatService, times(1)).processMessage(lobbyId, "TEAM_RED", "/RED", verifiedChatDto);
  }

  @Test
  void testSendTeamMessage_sendToDifferentTeam_throwException() {
    when(lobbyService.getPlayerTeam(realUsername, lobbyId)).thenReturn(blueTeam);

    assertThrows(
        IllegalStateException.class,
        () -> chatController.sendTeamMessage(lobbyId, redTeam, chatDto));
  }

  @Test
  void testSendTeamOperativeMessage_valid() {
    when(lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId)).thenReturn(blueTeam);
    when(lobbyService.getPlayerRole(chatDto.senderUsername(), lobbyId)).thenReturn(Role.OPERATIVE);

    chatController.sendTeamOperativeMessage(lobbyId, blueTeam, chatDto);

    ChatDto verifiedChatDto = new ChatDto(chatDto.senderUsername(), chatDto.content(), chatDto.type());
    verify(chatService, times(1))
        .processMessage(lobbyId, "OPERATIVE_BLUE", "/BLUE/operative", verifiedChatDto);
  }

  @Test
  void testSendTeamOperativeMessage_wrongRoleCorrectTeam() {
    when(lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId)).thenReturn(blueTeam);
    when(lobbyService.getPlayerRole(chatDto.senderUsername(), lobbyId)).thenReturn(Role.SPYMASTER);

    assertThrows(
        IllegalStateException.class,
        () -> chatController.sendTeamOperativeMessage(lobbyId, blueTeam, chatDto));
  }

  @Test
  void testSendTeamOperativeMessage_wrongTeamCorrectRole() {
    when(lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId)).thenReturn(redTeam);
    when(lobbyService.getPlayerRole(chatDto.senderUsername(), lobbyId)).thenReturn(Role.OPERATIVE);

    assertThrows(
        IllegalStateException.class,
        () -> chatController.sendTeamOperativeMessage(lobbyId, blueTeam, chatDto));
  }

  @Test
  void testSendTeamOperativeMessage_wrongRoleWrongTeam() {
    when(lobbyService.getPlayerTeam(chatDto.senderUsername(), lobbyId)).thenReturn(redTeam);
    when(lobbyService.getPlayerRole(chatDto.senderUsername(), lobbyId)).thenReturn(Role.SPYMASTER);

    assertThrows(
        IllegalStateException.class,
        () -> chatController.sendTeamOperativeMessage(lobbyId, blueTeam, chatDto));
  }
}
