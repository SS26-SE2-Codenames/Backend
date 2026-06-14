ALTER TABLE player
    ADD COLUMN uuid VARCHAR(36);

UPDATE player
SET uuid = gen_random_uuid()::text;

ALTER TABLE player
    ALTER COLUMN uuid SET NOT NULL;

ALTER TABLE player
    ADD CONSTRAINT unique_player_uuid
        UNIQUE (uuid);