-- Name --> Type --> Default --> Nullability --> Constraint

CREATE TABLE lobby
(
    lobby_code  VARCHAR(5)                      PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE     DEFAULT NOW() NOT NULL -- plan is to use this to delete stale lobbies
);

CREATE TABLE player
(
    id          BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lobby_code  VARCHAR(5)      NOT NULL REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    username    VARCHAR(20)     NOT NULL,
    is_host     BOOLEAN         DEFAULT FALSE NOT NULL,
    team        VARCHAR(4)      NOT NULL,
    role        VARCHAR(9)      NOT NULL,

    CONSTRAINT check_player_team
        CHECK (team IN ('RED', 'BLUE')),
    CONSTRAINT check_player_role
        CHECK (role IN ('OPERATIVE', 'SPYMASTER')),
    CONSTRAINT unique_player_per_lobby -- ask team if username in lobby has to be unique or not
        UNIQUE (lobby_code, username)
);


CREATE TABLE game_state
(
    lobby_code          VARCHAR(5)      PRIMARY KEY REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    current_turn        VARCHAR(4)      NOT NULL,
    current_phase       VARCHAR(9)      NOT NULL,
    clue_word           VARCHAR(20),
    clue_guess_amount   INT             DEFAULT 0,
    remaining_guesses   INT             DEFAULT 0,

    CONSTRAINT check_game_turn
        CHECK (current_turn IN ('RED', 'BLUE')),
    CONSTRAINT check_game_phase
        CHECK (current_phase IN ('SPYMASTER', 'OPERATIVE'))
);

-- each lobby will have 25 entries in the card table
CREATE TABLE card
(
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lobby_code      VARCHAR(5)      NOT NULL REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    position        INT             NOT NULL,
    word            VARCHAR(11)     NOT NULL,
    color           VARCHAR(8)      NOT NULL,
    is_guessed      BOOLEAN         DEFAULT FALSE NOT NULL,

    CONSTRAINT check_card_position
        CHECK (position BETWEEN 0 AND 24),
    CONSTRAINT check_card_color
        CHECK (color IN ('RED', 'BLUE', 'NEUTRAL', 'ASSASSIN')),
    CONSTRAINT unique_card_position_per_game
        UNIQUE (lobby_code, position)
);