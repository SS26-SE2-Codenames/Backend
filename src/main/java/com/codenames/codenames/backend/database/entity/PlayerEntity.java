package com.codenames.codenames.backend.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
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
  @Column(nullable = false, unique = true)
  private String uuid;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
