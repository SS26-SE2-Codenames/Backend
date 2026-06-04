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
@Table(name = "card")
public class Card {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(name = "lobby_code", length = 5, nullable = false)
  private String lobbyCode;
  @Column(nullable = false)
  private int position;
  @Column(length = 11, nullable = false)
  private String word;
  @Column(length = 8, nullable = false)
  private String color;
  @Column(name = "is_guessed", nullable = false)
  private Boolean isGuessed;
}
