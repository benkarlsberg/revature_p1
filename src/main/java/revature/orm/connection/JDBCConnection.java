package revature.orm.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnection {


    /*
    Singleton - only want one instance of object
                by privatizing constructor
     */
    private static Connection conn = null;

    public static Connection getConnection() {
        /*
        establishes new connection if one doesn't exist,
        otherwise return the current connection

        Credentials: url (endpoint), username, password
         */
        if (conn == null) {
            // establish new connection
            Properties props = new Properties();
            try {

                props.load(JDBCConnection.class.getClassLoader().getResourceAsStream("connection.properties"));
                // props.load(new FileReader("src/main/resources/connection.properties"));

                String endpoint = props.getProperty("endpoint");
                //URL Format (Postgresql JDBC)
                // jdbc:postgresql://[endpoint]/[database]
                String url = "jdbc:postgresql://" + endpoint + "/postgres";
                String username = props.getProperty("username");
                String password = props.getProperty("password");

                conn = DriverManager.getConnection(url, username, password);

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    /* TESTING PURPOSES !: */
    public static void main(String[] args) {
        Connection conn1 = getConnection();
        System.out.println(conn1);
    }

}
