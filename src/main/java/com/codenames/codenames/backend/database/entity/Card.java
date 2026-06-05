package com.codenames.codenames.backend.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistent entity representing a card record within the database schema.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "card")
public class Card {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "lobby_code", nullable = false)
  private Lobby lobby;
  @Column(nullable = false)
  private int position;
  @Column(length = 11, nullable = false)
  private String word;
  @Column(length = 8, nullable = false)
  private String color;
  @Column(name = "is_guessed", nullable = false)
  private Boolean isGuessed;
}
