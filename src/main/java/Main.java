import io.github.cdimascio.dotenv.Dotenv;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


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

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        String strOutput = result.toString();

        // Parse the String into a JSON object
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(strOutput);
    }

    // Buffered reader for the API call
    private static BufferedReader getBufferedReader() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-H",
                "Authorization: Bearer " + Controller.API_KEY,
                "https://api.clashofclans.com/v1/clans/%23GOQ98RQL/currentwar"
        );

        // Start the process
        Process process = pb.start();

        // Read the output

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader;
    }


}
