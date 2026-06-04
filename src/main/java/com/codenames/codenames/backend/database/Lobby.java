package com.codenames.codenames.backend.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lobby")
public class Lobby {
  @Id
  @Column(name = "lobby_code", length = 5, nullable = false)
  private String lobbyCode;
  @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
  private java.sql.Timestamp createdAt;
}
