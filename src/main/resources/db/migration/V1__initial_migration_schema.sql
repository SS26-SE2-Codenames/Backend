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
