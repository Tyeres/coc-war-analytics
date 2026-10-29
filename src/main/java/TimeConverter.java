import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConverter {


    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS'Z'");

    /**
     * Parses a compact ISO-8601 UTC string into a LocalDateTime (UTC),
     * suitable for a MySQL DATETIME column.
     */
    // TODO Create column: end_time_utc DATETIME(3) NOT NULL
    protected static LocalDateTime toLocalDateTime(String input) {
        return LocalDateTime.parse(input, INPUT_FORMAT);
    }
}