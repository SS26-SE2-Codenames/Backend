package com.codenames.codenames_backend.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SerializationJSON {
  private final ObjectMapper mapper = new ObjectMapper();

  public String serialize(GameStateDataTransferObject gameStateDataTransferObject) {
    try {
      String jsonNode = mapper.writeValueAsString(gameStateDataTransferObject);
      return jsonNode;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
