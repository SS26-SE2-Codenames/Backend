-- add uuid to player
ALTER TABLE player
    ADD COLUMN uuid VARCHAR(36);

UPDATE player
SET uuid = gen_random_uuid()::text;

ALTER TABLE player
    ALTER COLUMN uuid SET NOT NULL;

ALTER TABLE player
    ADD CONSTRAINT unique_player_uuid
        UNIQUE (uuid);

-- add cheat flags to game state
ALTER TABLE game_state
    ADD COLUMN red_team_cheat_used BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE game_state
    ADD COLUMN blue_team_cheat_used BOOLEAN DEFAULT FALSE NOT NULL;