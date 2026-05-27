package com.codenames.codenames.backend.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codenames.codenames.backend.game.api.dto.CardDataTransferObject;
import com.codenames.codenames.backend.game.api.dto.ClueDto;
import com.codenames.codenames.backend.game.api.dto.GameStateDataTransferObject;
import com.codenames.codenames.backend.game.domain.Card;
import com.codenames.codenames.backend.game.domain.Color;
import com.codenames.codenames.backend.lobby.domain.Role;
import com.codenames.codenames.backend.lobby.domain.Team;
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
  private static final Team redTeam = Team.RED;
  private static final Role spymaster = Role.SPYMASTER;

  @BeforeEach
  void setUp() {
    card = new Card("TEST", Color.RED);
    serializer = new SerializationJson(mapper);

    dummyList = List.of(new CardDataTransferObject("TEST", null, false));
    dummyGameState =
        new GameStateDataTransferObject(
            redTeam, redTeam, spymaster, new ClueDto("Test", 1), dummyList);
  }

  @Test
  void testSerialize_pass() {
    String expectedResult =
        """
            {"winner":"RED","currentTurn":"RED","currentPhase":"SPYMASTER","currentClue":{"word":"Test","guessAmount":1},"cardList":[{"word":"TEST","color":null,"isGuessed":false}]}""";
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
  // We then create this custom exception and throw it. (Solution found on stack overflow)
  static class MockJsonProcessingException extends JsonProcessingException {
    public MockJsonProcessingException(String message) {
      super(message);
    }
  }
}
