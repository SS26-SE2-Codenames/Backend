package com.codenames.codenames.backend.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistent entity representing a lobby record within the database schema.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lobby")
public class LobbyEntity {
  @Id
  @Column(name = "lobby_code", length = 5, nullable = false)
  private String lobbyCode;
  @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
  private Timestamp createdAt;

  @OneToOne(mappedBy = "lobbyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private GameStateEntity gameStateEntity;

  @OneToMany(mappedBy = "lobbyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlayerEntity> playerEntities = new ArrayList<>();

  @OneToMany(mappedBy = "lobbyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CardEntity> cardEntities = new ArrayList<>();
}
