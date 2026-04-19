package com.codenames.codenames_backend.chat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatHistory {
  private final List<ChatDto> globalChat = new CopyOnWriteArrayList<>();
  private final List<ChatDto> redTeamChat = new CopyOnWriteArrayList<>();
  private final List<ChatDto> blueTeamChat = new CopyOnWriteArrayList<>();

  private static final int MAX_MESSAGES = 50;

  public void addLobbyMessage(ChatDto chatDto){
    addMessage(globalChat, chatDto);
  }

  public void addTeamMessage(String team, ChatDto chatDto){
    if("RED".equalsIgnoreCase(team)){
      addMessage(redTeamChat, chatDto);
    } else if ("BLUE".equalsIgnoreCase(team)){
      addMessage(blueTeamChat, chatDto);
    }
    else {
      throw new IllegalArgumentException("Unknown Team: " + team);
    }
  }

  private void addMessage(List<ChatDto> target, ChatDto chatDto){
    target.add(chatDto);
    if (target.size() > MAX_MESSAGES){
      target.remove(0);
    }
  }
}
