package com.codenames.codenames.backend.database.repository;

import com.codenames.codenames.backend.database.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for Lobby that has the default available methods needed to perform SQL queries.
 */
@Repository
public interface LobbyRepository extends JpaRepository<Lobby, String> {}
