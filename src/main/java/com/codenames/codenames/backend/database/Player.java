package com.codenames.codenames.backend.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "lobby_code", nullable = false)
  private Lobby lobby;
  @Column(length = 20, nullable = false)
  private String username;
  @Column(name = "is_host", nullable = false)
  private Boolean isHost;
  @Column(length = 4, nullable = false)
  private String team;
  @Column(length = 9, nullable = false)
  private String role;
}
