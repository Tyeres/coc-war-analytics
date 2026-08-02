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
    }
    // Any players in war that have never been added to the database must first be added
    // THIS METHOD IS UNTESTED!
    public void updatePlayerRecord(JSONObject jsonObject) throws SQLException {
        // Get the member list from the JSON
        JSONArray memberList = (JSONArray) (((JSONObject) jsonObject.get("clan")).get("members"));
        // Prepare an object for executing SQL queries
        Statement statement = this.con.createStatement();

        // Check if each member is within the database. If not, add him.
        for (Object memberObject : memberList) {
            JSONObject memberJSON = (JSONObject) memberObject;
            String playerTag = (String) memberJSON.get("tag");
            ResultSet resultSet = statement.executeQuery("SELECT " + playerTag + " FROM Player;");
            // If a result was not found (the player was not found within the database)
            if (!resultSet.next()) {
                // We need to add the user to the database
                String playerName = (String) memberJSON.get("name");
                statement.executeQuery("INSERT INTO Player VALUES (" + playerTag + ", " + playerName + ");");
                System.out.println(ConsoleColors.BLUE + "Player " + playerName + ": " + playerTag + " added to the database." + ConsoleColors.RESET);
            }
        }
    }
}
