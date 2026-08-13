// https://www.geeksforgeeks.org/java/establishing-jdbc-connection-in-java/

import org.json.simple.*;

import java.sql.*;

// Java Database Connectivity class.
public class JDBC {
    // The mySQL connection object
    private final Connection con;

    private final static String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATA = "clash"; // Database name
    private static final String USER = "root";
    private static final String PASS = "mysql";

    public JDBC() throws SQLException {
        // Set the database connection.
        // Uses mySQL
        this.con = DriverManager.getConnection(
                "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATA,
                USER,
                PASS
        );
    }


    // Add the newest war to the database
    public void update(JSONObject jsonObject) throws SQLException {
        // Do not add any data to the database unless the war has ended.
        if (!jsonObject.get("state").equals("warEnded")) {
            System.out.println("The clan is not in a 'war ended' state. No updates completed.");
            return; // Do not do anything
        }
        // Check if the clan war has already been added to the database.
        if (checkClanWarUpdateStatus(jsonObject)) {
            System.out.println("The current clan war has already been added to the database. No updates completed.");
            return;
        }
        // Update list of players (update Player table)
        updatePlayerRecord(jsonObject);
        System.out.println("--------------------------");
        // Update the enemy clan into the database (update EnemyClan table)
        updateEnemyClan(jsonObject);
        System.out.println("--------------------------");
        // Update the clan war in the database (update ClanWar table)
        updateClanWar(jsonObject);
        System.out.println("--------------------------");
        // Update the WarParticipation table
        updateWarParticipation(jsonObject);
        System.out.println("--------------------------");
        // Update WarAttack table
        updateWarAttack(jsonObject);
        System.out.println(ConsoleColors.YELLOW + "Update completed with no problems." + ConsoleColors.RESET);
    }

    // Any players in war that have never been added to the database must first be added
    private void updatePlayerRecord(JSONObject jsonObject) throws SQLException {
        // Get the member list from the JSON
        JSONArray memberList = (JSONArray) (((JSONObject) jsonObject.get("clan")).get("members"));
        // Prepare an object for executing SQL queries
        Statement statement = this.con.createStatement();
        System.out.println(memberList);
        // Check if each member is within the database. If not, add him.
        for (Object memberObject : memberList) {
            // Cast to JSONObject (we are iterating through a JSON array of JSON objects)
            JSONObject memberJSON = (JSONObject) memberObject;
            // Get the player tag
            String playerTag = (String) memberJSON.get("tag");
            // Get the player name
            String playerName = (String) memberJSON.get("name");
            // Query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Player WHERE Player_tag = '" + playerTag + "';");
            // If a result was not found (the player was not found within the database)
            if (!resultSet.next()) {
                statement.execute("INSERT INTO Player VALUES ('" + playerTag + "', '" + playerName + "');");
                System.out.println(ConsoleColors.BLUE + "Player " + playerName + ": " + playerTag + " added to the database." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.BLUE + "Player " + playerName + " with tag " + playerTag + " already in database. No action taken." + ConsoleColors.RESET);
            }
            // If a result is found, nothing is done
        }
    }

    // Updates the enemy clan into the database.
    private void updateEnemyClan(JSONObject jsonObject) throws SQLException {
        // Get the war clan tag
        JSONObject jsonOpponent = (JSONObject) jsonObject.get("opponent");
        // Get the clan tag
        String enemyClanTag = (String) (jsonOpponent.get("tag"));
        // Get the clan name
        String clanName = (String) jsonOpponent.get("name");

        // Prepare an object for executing SQL queries
        Statement statement = this.con.createStatement();

        // Check if the enemy clan is within the database. If not, add the clan.
        ResultSet resultSet = statement.executeQuery("SELECT * FROM EnemyClan WHERE Enemy_clan_tag = '" + enemyClanTag + "';");
        // If a result is not found, then add the clan into the database. If so, do not add it; we have no reason to because it's already in the database.
        if (!resultSet.next()) {
            statement.execute("INSERT INTO EnemyClan VALUES ('" + enemyClanTag + "', '" + clanName + "');");
            System.out.println(ConsoleColors.GREEN + "Clan " + clanName + ": " + enemyClanTag + " added to the database." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.GREEN + "Clan: " + clanName + " with tag " + enemyClanTag + " already in database. No action taken." + ConsoleColors.RESET);
        }
        // A match was found. This clan was fought before. Do nothing.
    }

    // Adds the current clan war to the database.
    // Method is currently untested
    private void updateClanWar(JSONObject jsonObject) throws SQLException {
        // The checkClanWarUpdateStatus method should have already been used before this to verify that the current clan
        // war has not been added to the database. This means we can assume we do not have to check for duplicates before SQL insertions.

        // The API returns a long when this should really be an int. We just cast it.
        int warSize = (int) ((long) jsonObject.get("teamSize"));
        String startTime = (String) jsonObject.get("startTime");
        String endTime = (String) jsonObject.get("endTime");
        String enemyClanTag = (String) ((JSONObject) jsonObject.get("opponent")).get("tag");
        // Add all the fields to the database
        Statement statement = this.con.createStatement();
        statement.execute("INSERT INTO ClanWar (War_size, War_start_time, War_end_time, Enemy_clan_tag) VALUES (" +
                warSize + ", '" + startTime + "', '" + endTime + "', '" + enemyClanTag + "');");
        System.out.println(ConsoleColors.GREEN + "Clan war table successfully updated." + ConsoleColors.RESET);
    }

    // Updates the WarParticipation table in the database.
    // The checkClanWarUpdateStatus method should have already been used before this to verify that the current clan
    // war has not been added to the database. This means we can assume we do not have to check for duplicates before SQL insertions.
    private void updateWarParticipation(JSONObject jsonObject) throws SQLException {
        // Iterate through each player, because we are logging the war participation for each player in the clan war.
        // Get the JSONArray of players
        JSONArray jsonArrayOfPlayers = (JSONArray) ((JSONObject) jsonObject.get("clan")).get("members");
        // Get the Clan_war_id for the current war
        int warID = getClanWarID(jsonObject);
        // Iterate through the array of players and add each of their clan war participation to the WarParticipation table in the database
        int i = 0; // Counter to see how many times loop ran for analytics
        for (Object playerObject : jsonArrayOfPlayers) {
            // We cast the player json object to a json object
            JSONObject jsonPlayer = (JSONObject) playerObject;
            String playerTag = (String) jsonPlayer.get("tag");
            // The API returns a long when this should be an int
            int mapPos = (int) ((long) jsonPlayer.get("mapPosition"));
            // The API returns a long when this should be an int
            int townHallLvl = (int) ((long) jsonPlayer.get("townhallLevel"));
            // Insert the data into the WarParticipation table
            Statement statement = this.con.createStatement();
            statement.execute("INSERT INTO WarParticipation VALUES ('" + playerTag + "', " + warID + ", " + mapPos + ", " + townHallLvl + ");");
            i++; // Increment counter
        }
        System.out.println(ConsoleColors.GREEN + "WarParticipation table successfully updated by updating " + i + " times." + ConsoleColors.RESET);
    }

    // Update the WarAttack table.
    private void updateWarAttack(JSONObject jsonObject) throws SQLException {
        // Get the id of the war
        int warID = getClanWarID(jsonObject);
        // We need to iterate through the list of players
        JSONArray listOfPlayers = (JSONArray) ((JSONObject) jsonObject.get("clan")).get("members");
        // Iterate through the list
        for (Object objPlayer : listOfPlayers) {
            // It is a list of JSON objects. So, we need to cast the objPlayer
            JSONObject jsonPlayer = (JSONObject) objPlayer;
            // Get the player tag
            String playerTag = (String) jsonPlayer.get("tag");
            String playerName = (String) jsonPlayer.get("name");
            // We now have to iterate through the list of attacks
            JSONArray listOfWarAttacks = (JSONArray) jsonPlayer.get("attacks");
            // Counter for which attack this is.
            /* I am doubtful this is an accurate way of knowing which attack number this is because I think the JSON
            might randomize the order. However, the JSON does not seem to specify which attack this is any other way. */
            int attackNumber = 0;
            // If the player used no war attacks, this list should be empty
            if (listOfWarAttacks != null) {
                for (Object warAttackObject : listOfWarAttacks) {
                    // Cast because it is a list of JSON war attack objects
                    JSONObject warAttackJSON = (JSONObject) warAttackObject;
                    attackNumber++; // Increment
                    // Get the database fields
                    // The stars for the attack
                    int stars = (int) ((long) warAttackJSON.get("stars"));
                    // Destruction percentage of the attack
                    int destructionPercentage = (int) ((long) warAttackJSON.get("destructionPercentage"));
                    // This tells which total order of attacks this is. It is a war‑wide attack index, not per‑player and not per‑target.
                    int order = (int) ((long) warAttackJSON.get("order"));
                    // The duration of the attacks in seconds
                    int duration = (int) ((long) warAttackJSON.get("duration"));
                    // Insert the attack into the database
                    Statement statement = this.con.createStatement();
                    statement.execute("INSERT INTO WarAttack VALUES ('" + playerTag + "', " + warID + ", " + attackNumber + ", " +
                            stars + ", " + destructionPercentage + ", " + order + ", " + duration + ");");
                    System.out.print(ConsoleColors.GREEN + "Attack " + attackNumber + " for Player " + playerTag + ": " + playerName +" added to WarAttack table successfully. | ");
                }
                System.out.println(ConsoleColors.RESET);
            }
            else {
                System.out.println(ConsoleColors.RED + jsonPlayer.get("name") + " missed his war attack. No war attacks added for him." + ConsoleColors.RESET);
            }
        }
    }

    // This method returns true if the war participation table has already been updated with the current war. If so, then the current war has already been added to the database.
    private boolean checkClanWarUpdateStatus(JSONObject jsonObject) throws SQLException {
        // We need to know if the clan war has been added to the database already.
        // We will do this by checking if there is a clan war with the same start time as the current clan war
        String startTime = (String) jsonObject.get("startTime");

        // Prepare an object for executing SQL query
        Statement statement = this.con.createStatement();

        // Check if the clan war has already been added to the database.
        // It really does not matter what we select. We just need to know if a result is found.
        ResultSet resultSet = statement.executeQuery("SELECT War_start_time FROM ClanWar WHERE War_start_time = '" + startTime + "';");

        // Return whether the current is in the database already or not.
        return resultSet.next();
    }

    // Returns the id for the current clan war
    protected int getClanWarID(JSONObject jsonObject) throws SQLException {
        // We will find the clan war by using the startTime
        String startTime = (String) jsonObject.get("startTime");
        // Query for the ID using this startTime
        Statement statement = this.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Clan_war_id FROM ClanWar WHERE War_start_time = '" + startTime + "';");
        // We have to move to the first index (default has none selected)
        resultSet.next();
        // JDBC’s ResultSet follows SQL conventions, where columns are numbered starting at 1.
        // We return the Clan_war_id, which is on column 1.
        return resultSet.getInt(1);
    }
}
