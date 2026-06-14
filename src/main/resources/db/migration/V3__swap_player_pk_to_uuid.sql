-- Swap player PK from auto-generated BIGINT id to application-generated UUID
-- No other table references player.id, so no FK rewiring needed

ALTER TABLE player
    DROP COLUMN id CASCADE;

ALTER TABLE player
    ADD PRIMARY KEY (uuid);

-- V2 Constraint no longer needed since i set it to PK
ALTER TABLE player
    DROP CONSTRAINT unique_player_uuid;
