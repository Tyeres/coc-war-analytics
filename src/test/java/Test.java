import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) {
        try {
            JDBC jdbc = new JDBC();
            Connection con = jdbc.getCon();
            Statement statement = con.createStatement();
            statement.execute("INSERT INTO Player VALUES ('#agdh', 'Jonathan');");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Player;");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("Player_tag"));
                System.out.println(resultSet.getString("Player_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
