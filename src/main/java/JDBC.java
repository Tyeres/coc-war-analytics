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
        if (!jsonObject.get("state").equals("ended")) {
            System.out.println(ConsoleColors.RED + "The clan is not in a 'war ended' state. No updates completed." + ConsoleColors.RESET);
            return; // Do not do anything
        }
        // Update list of players
        updatePlayerRecord(jsonObject);
        // Update the enemy clan into the database
        updateEnemyClan(jsonObject);
    }
    // Any players in war that have never been added to the database must first be added
    // THIS METHOD IS UNTESTED!
    private void updatePlayerRecord(JSONObject jsonObject) throws SQLException {
        // Get the member list from the JSON
        JSONArray memberList = (JSONArray) (((JSONObject) jsonObject.get("clan")).get("members"));
        // Prepare an object for executing SQL queries
        Statement statement = this.con.createStatement();

        // Check if each member is within the database. If not, add him.
        for (Object memberObject : memberList) {
            // Cast to JSONObject (we are iterating through a JSON array of JSON objects)
            JSONObject memberJSON = (JSONObject) memberObject;
            // Get the player tag
            String playerTag = (String) memberJSON.get("tag");
            // Query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Player WHERE Player_tag = '" + playerTag + "';");
            // If a result was not found (the player was not found within the database)
            if (!resultSet.next()) {
                // We need to add the user to the database
                String playerName = (String) memberJSON.get("name");
                statement.executeQuery("INSERT INTO Player VALUES (" + playerTag + ", " + playerName + ");");
                System.out.println(ConsoleColors.BLUE + "Player " + playerName + ": " + playerTag + " added to the database." + ConsoleColors.RESET);
            }
            // If a result is found, nothing is done
        }
    }
    // Updates the enemy clan into the database.
    // Untested method
    private void updateEnemyClan(JSONObject jsonObject) throws SQLException {
        // Get the war clan tag
        JSONObject jsonOpponent = (JSONObject) jsonObject.get("opponent");
        String enemyClanTag = (String) (jsonOpponent.get("tag"));

        // Prepare an object for executing SQL queries
        Statement statement = this.con.createStatement();

        // Check if the enemy clan is within the database. If not, add the clan.
        ResultSet resultSet = statement.executeQuery("SELECT * FROM EnemyClan WHERE Enemy_clan_tag = '" + enemyClanTag + "';");
        // If a result is not found, then add the clan into the database. If so, do not add it; we have no reason to because it's already in the database.
        if (!resultSet.next()) {
            // Get the name of the clan too and add the clan into the database
            String clanName = (String) jsonOpponent.get("name");
            statement.executeQuery("INSERT INTO EnemyClan VALUES (" + enemyClanTag + ", " + clanName + ");");
            System.out.println(ConsoleColors.GREEN + "Clan " + clanName + ": " + enemyClanTag + " added to the database." + ConsoleColors.RESET);
        }
        // A match was found. This clan was fought before. Do nothing.
    }
}
