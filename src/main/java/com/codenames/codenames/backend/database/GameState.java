package com.codenames.codenames.backend.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistent entity representing a gameState record within the database schema.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "game_state")
public class GameState {
  @Id
  @Column(name = "lobby_code")
  private String lobbyCode;
  @OneToOne
  @MapsId
  @JoinColumn(name = "lobby_code")
  private Lobby lobby;
  @Column(name = "current_turn", length = 4, nullable = false)
  private String currentTurn;
  @Column(name = "current_phase", length = 9, nullable = false)
  private String currentPhase;
  @Column(name = "clue_word", length = 20)
  private String clueWord;
  @Column(name = "clue_guess_amount")
  private int clueGuessAmount;
  @Column(name = "remaining_guesses")
  private int remainingGuesses;
}
