package com.codenames.codenames_backend.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/** Service to serialize the game state DTO into JSON, which is then sent to the frontend. */
@Component
public class SerializationJson {
  private final ObjectMapper mapper;

  public SerializationJson(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Method to serialize the game state DTO into JSON.
   *
   * @param gameStateDataTransferObject the DTO of the game state
   * @return the string JSON containing: winner, points, list of cards and their information
   */
  public String serialize(GameStateDataTransferObject gameStateDataTransferObject) {
    try {
      return mapper.writeValueAsString(gameStateDataTransferObject);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error while parsing into JSON.", e);
    }
  }
}
