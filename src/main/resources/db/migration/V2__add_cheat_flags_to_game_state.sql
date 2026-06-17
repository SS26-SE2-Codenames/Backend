ALTER TABLE game_state
    ADD COLUMN red_team_cheat_used BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE game_state
    ADD COLUMN blue_team_cheat_used BOOLEAN DEFAULT FALSE NOT NULL;