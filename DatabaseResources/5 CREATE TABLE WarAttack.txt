CREATE TABLE WarAttack (
    Player_tag VARCHAR(10) NOT NULL,
    Clan_war_id int NOT NULL,
    War_attack_number TINYINT CHECK (War_attack_number = 1 OR War_attack_number = 2) NOT NULL,
    Stars TINYINT NOT NULL CHECK (Stars >= 0 AND Stars <= 3),
    Destruction_percentage TINYINT NOT NULL CHECK (Destruction_percentage >= 0 AND Destruction_percentage <= 100),
    Order_ TINYINT,
    Duration TINYINT NOT NULL,
    PRIMARY KEY(Player_tag, Clan_war_id, War_attack_number),
    FOREIGN KEY (Player_tag, Clan_war_id) REFERENCES WarParticipation(Player_tag, Clan_war_id) ON DELETE CASCADE
    );