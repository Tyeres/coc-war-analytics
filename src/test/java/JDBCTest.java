import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

class JDBCTest {
    @Test
    void checkGet() {
        try {
            JDBC jdbc = new JDBC();
            Connection con = jdbc.getCon();
            Statement statement = con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM WarAttack WHERE Player_tag = '#JUYQLUVJ' AND War_attack_number = 1;");
            result.next();
            Assertions.assertEquals(result.getString(1), "#JUYQLUVJ");
            Assertions.assertEquals(result.getInt(2), 1);
            Assertions.assertEquals(result.getInt(3), 1);
            Assertions.assertEquals(result.getInt(4), 3);
            Assertions.assertEquals(result.getInt(5), 100);
            Assertions.assertEquals(result.getInt(6), 1);
            Assertions.assertEquals(result.getInt(7), 20);
            Assertions.assertEquals(result.getString("Player_tag"), "#JUYQLUVJ");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}