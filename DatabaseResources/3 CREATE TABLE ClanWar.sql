CREATE TABLE ClanWar (
    Clan_war_id int AUTO_INCREMENT PRIMARY KEY,
    War_size int NOT NULL,
    War_start_time DATETIME UNIQUE NOT NULL,
    War_end_time DATETIME UNIQUE NOT NULL,
    Enemy_clan_tag VARCHAR(30) NOT NULL,
    FOREIGN KEY (Enemy_clan_tag) REFERENCES enemyclan(Enemy_clan_tag) ON DELETE CASCADE
    );
    