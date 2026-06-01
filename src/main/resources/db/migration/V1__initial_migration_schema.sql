-- Name --> Type --> Default --> Nullability --> Constraint

CREATE TABLE lobby
(
    lobby_code  VARCHAR(255)    PRIMARY KEY,
    status      VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE player
(
    id          SERIAL          PRIMARY KEY,
    lobby_code  VARCHAR(255)    NOT NULL REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    username    VARCHAR(255)    NOT NULL,
    is_host     BOOLEAN         DEFAULT FALSE NOT NULL,
    team        VARCHAR(255)    NOT NULL,
    role        VARCHAR(255)    NOT NULL,

    CONSTRAINT check_player_team
        CHECK (team IN ('RED', 'BLUE')),
    CONSTRAINT check_player_role
        CHECK (role IN ('OPERATIVE', 'SPYMASTER')),
    CONSTRAINT unique_player_per_lobby -- ask team if username in lobby has to be unique or not
        UNIQUE (lobby_code, username)
);

CREATE TABLE game_state
(
    id                  SERIAL          PRIMARY KEY,
    lobby_code          VARCHAR(255)    NOT NULL REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    current_turn        VARCHAR(255)    NOT NULL,
    current_phase       VARCHAR(255)    NOT NULL,
    clue_word           VARCHAR(255),
    clue_guess_amount   INT             DEFAULT 0,
    remaining_guesses   INT             DEFAULT 0,

    CONSTRAINT check_game_turn
        CHECK (current_turn IN ('RED', 'BLUE')),
    CONSTRAINT check_game_phase
        CHECK (current_phase IN ('SPYMASTER', 'OPERATIVE')),
    CONSTRAINT unique_game_per_lobby
        UNIQUE (lobby_code)
);