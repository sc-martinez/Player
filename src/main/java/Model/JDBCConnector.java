package Model;

import java.sql.*;


public class JDBCConnector  {


    private static Connection conn = null;

    public static void connect() throws IllegalAccessException,InstantiationException,SQLException,ClassNotFoundException{
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/player","postgres", "password");
    }

    public static void disconnect() throws SQLException {
        conn.close();
    }
}
