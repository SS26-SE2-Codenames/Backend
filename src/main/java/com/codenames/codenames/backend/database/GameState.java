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
@Table(name = "game_state")
public class GameState {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String lobbyCode;
  @Column(name = "current_turn", length = 4, nullable = false)
  private String currentTurn;
  @Column(name = "current_phase", length = 9, nullable = false)
  private String currentPhase;
  @Column(name = "clue_word", length = 20)
  private String clueWord;
  @Column(name = "clue_guess_amount")
  private long clueGuessAmount;
  @Column(name = "remaining_guesses")
  private long remainingGuesses;
}
