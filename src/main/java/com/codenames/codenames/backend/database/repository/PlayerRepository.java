package com.codenames.codenames.backend.database.repository;

import com.codenames.codenames.backend.database.entity.PlayerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for Lobby that has the default available methods needed to perform SQL queries.
 */
@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {

  /**
   * Custom SQL query that is not included in the JpaRepository interface.
   *
   * @param lobbyCode the 5 character lobby code we want to search the player in
   * @param username the username of the player we want to search for
   * @return the player
   */
  // First Lobby is the Lobby object inside the entity where we can find the lobby_code String.
  // And "resets" the SQL search back to the root table Player
  Optional<PlayerEntity> findByLobbyEntityLobbyCodeAndUsername(String lobbyCode, String username);
}
