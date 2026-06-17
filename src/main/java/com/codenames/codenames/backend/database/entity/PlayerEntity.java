package com.codenames.codenames.backend.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistent entity representing a player record within the database schema.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player")
public class PlayerEntity {
  @Id
  @Column(nullable = false)
  private String uuid;
  @ManyToOne
  @JoinColumn(name = "lobby_code", nullable = false)
  private LobbyEntity lobbyEntity;
  @Column(length = 20, nullable = false)
  private String username;
  @Column(name = "is_host", nullable = false)
  private Boolean isHost;
  @Column(length = 4, nullable = false)
  private String team;
  @Column(length = 9, nullable = false)
  private String role;
}
