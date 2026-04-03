package com.codenames.codenames_backend.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames_backend.playingfield.Card;
import com.codenames.codenames_backend.playingfield.Color;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerializationJsonTest {

  SerializationJson serializer;
  Card card;
  List<CardDataTransferObject> dummyList;
  GameStateDataTransferObject dummyGameState;
  ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    card = new Card("TEST", Color.RED);
    serializer = new SerializationJson(mapper);

    dummyList = List.of(new CardDataTransferObject("TEST", "HIDDEN", false));
    dummyGameState = new GameStateDataTransferObject("RED", "RED", 0, 0, dummyList);
  }

  @Test
  void testSerialize_pass() {
    String expectedResult =
        "{\"winner\":\"RED\",\"currentTurn\":\"RED\",\"currentRedFound\":0,\"currentBlueFound\":0,"
            + "\"cardList\":[{\"word\":\"TEST\",\"color\":\"HIDDEN\",\"isGuessed\":false}]}";
    String result = serializer.serialize(dummyGameState);
    assertEquals(expectedResult, result);
  }

  // writeValueAsString throws JsonProcessingException,
  // I either need try catch block or have my method throw the exception.
  @Test
  void testSerialize_exception() throws JsonProcessingException {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    SerializationJson exceptionSerializer = new SerializationJson(mockMapper);
    var exception = new MockJsonProcessingException("Because of protected constructors");
    when(mockMapper.writeValueAsString(any())).thenThrow(exception);
    assertThrows(IllegalStateException.class, () -> exceptionSerializer.serialize(null));
  }

  // Testing exception for writeValueAsString requires passing objectMapper in a constructor.
  // We then create this custom exception and throw it. (Solution fround on stack overflow)
  static class MockJsonProcessingException extends JsonProcessingException {
    public MockJsonProcessingException(String message) {
      super(message);
    }
  }
}
