-- Name --> Type --> Default --> Nullability --> Constraint

CREATE TABLE lobby
(
    lobby_code  VARCHAR(255)                    PRIMARY KEY,
    status      VARCHAR(255)                    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE     DEFAULT NOW() NOT NULL -- plan is to use this to delete stale lobbies
);

CREATE TABLE player
(
    id          BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
    lobby_code          VARCHAR(255)    PRIMARY KEY REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    current_turn        VARCHAR(255)    NOT NULL,
    current_phase       VARCHAR(255)    NOT NULL,
    clue_word           VARCHAR(255),
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
    lobby_code      VARCHAR(255)    NOT NULL REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    position        INT             NOT NULL,
    word            VARCHAR(255)    NOT NULL,
    color           VARCHAR(255)    NOT NULL,
    is_guessed      BOOLEAN         DEFAULT FALSE NOT NULL,

    CONSTRAINT check_card_position
        CHECK (position BETWEEN 0 AND 24),
    CONSTRAINT check_card_color
        CHECK (color IN ('RED', 'BLUE', 'NEUTRAL', 'ASSASSIN')),
    CONSTRAINT unique_card_position_per_game
        UNIQUE (lobby_code, position)
);

-- debatable if we even need this, will need to ask team
-- room_key is what ChatHistory uses as the map key (e.g. "lobby", "TEAM_RED", "OPERATIVE_BLUE").
CREATE TABLE chat_message
(
    id              BIGINT                          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lobby_code      VARCHAR(255)                    NOT NULL REFERENCES lobby (lobby_code) ON DELETE CASCADE,
    room_key        VARCHAR(255)                    NOT NULL,
    sender_username VARCHAR(255)                    NOT NULL,
    content         TEXT                            NOT NULL,
    message_type    VARCHAR(255)                    NOT NULL,
    sent_at         TIMESTAMP WITHOUT TIME ZONE     DEFAULT NOW() -- iirc, frontend plans on saving this?? backend might need to be refactored to store it as well if we decide to have chat logs in DB for retrieval... we would likely need to store time if we want frontend to display correct time
);