import io.github.cdimascio.dotenv.Dotenv;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Main {

    public static void main(String[] args) {


        // Load the api key
        Dotenv dotenv = Dotenv.load();
        Controller.API_KEY = dotenv.get("API_KEY");

        try {
            System.out.println(fetchJSON());
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static JSONObject fetchJSON() throws IOException, ParseException {
        // Prepare a commandline process to retrieve the war JSON data
        BufferedReader reader = getBufferedReader();

        // Read the buffered reader
        String strOutput = readBufferedReader(reader);

        // Parse the String into a JSON object
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(strOutput);
    }

    // Buffered reader for the API call
    private static BufferedReader getBufferedReader() throws IOException {
        String endpoint = "https://api.clashofclans.com/v1/clans/%23GOQ98RQL/currentwar";

        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + Controller.API_KEY);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            System.err.println("Connection failed. Check if current IP is allowed for the API key.\n" +
                    "Response Code: " + responseCode);
            System.exit(-1);
        }

        return new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
    }
    // Convert the buffered text to String
    private static String readBufferedReader(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
