package com.codenames.codenames.backend.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player")
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "lobby_code", length = 5, nullable = false)
  private String lobbyCode;
  @Column(length = 20, nullable = false)
  private String username;
  @Column(name = "is_host", nullable = false)
  private Boolean isHost;
  @Column(length = 4, nullable = false)
  private String team;
  @Column(length = 9, nullable = false)
  private String role;
}
