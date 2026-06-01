CREATE TABLE lobby
(
    lobby_code  VARCHAR(255)    PRIMARY KEY,
    status      VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL
);
