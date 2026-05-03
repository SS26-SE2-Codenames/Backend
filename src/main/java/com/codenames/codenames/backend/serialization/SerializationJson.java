package com.codenames.codenames.backend.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/** Service to serialize the game state DTO into JSON, which is then sent to the frontend. */
@Component
public class SerializationJson {
  private final ObjectMapper mapper;

  /**
   * Constructor for serialization (mapper passed as argument to enable mocking in tests).
   *
   * @param mapper the ObjectMapper object that is used to serialize into JSON
   */
  public SerializationJson(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Method to serialize the game state DTO into JSON.
   *
   * @param gameStateDataTransferObject the DTO of the game state
   * @return the string JSON containing: winner, points, list of cards and their information
   * @throws IllegalStateException if serialization fails
   */
  public String serialize(GameStateDataTransferObject gameStateDataTransferObject) {
    try {
      return mapper.writeValueAsString(gameStateDataTransferObject);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error while parsing into JSON.", e);
    }
  }
}
