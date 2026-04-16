# Codenames Backend

## Backend #11 - Turn Management and Game Flow

**Description:**  
This module is responsible for managing the complete turn-based logic of the Codenames game according to the official rules.

**Key Features Implemented:**
- Management of current team and game phase (Spymaster Clue Phase vs Operatives Guessing Phase)
- Strict validation of player actions based on their role (Spymaster vs Operative)
- Automatic team switching after each completed turn
- Support for giving clues, guessing cards, and passing
- Detection of Assassin card and win conditions
- Proper handling of remaining guesses (+1 rule)

**REST API Endpoints:**

| Method | Endpoint              | Description                        |
|--------|-----------------------|------------------------------------|
| POST   | `/api/game/clue`      | Spymaster submits a clue           |
| POST   | `/api/game/guess`     | Operative guesses a card           |
| POST   | `/api/game/pass`      | Pass the current turn              |
| GET    | `/api/game/state`     | Retrieve current game state        |

**Technologies:** Spring Boot, Java, JUnit 5, Spring Web