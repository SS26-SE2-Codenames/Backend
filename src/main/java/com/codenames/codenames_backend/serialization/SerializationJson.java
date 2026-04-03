package com.codenames.codenames_backend.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SerializationJson {
  private final ObjectMapper mapper = new ObjectMapper();

  public String serialize(GameStateDataTransferObject gameStateDataTransferObject) {
    try {
      return mapper.writeValueAsString(gameStateDataTransferObject);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
