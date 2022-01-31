package revature.orm.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
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
                //get path to connection.properties in resources folder
                URL res = JDBCConnection.class.getClassLoader().getResource("connection.properties");
                File file = Paths.get(res.toURI()).toFile();
                String absolutePath = file.getAbsolutePath();
                //System.out.println(absolutePath);
                InputStream input = new FileInputStream(absolutePath);

                props.load(input);
                // props.load(new FileReader("src/main/resources/connection.properties"));

                String endpoint = props.getProperty("endpoint");
                //URL Format (Postgresql JDBC)
                // jdbc:postgresql://[endpoint]/[database]
                String url = "jdbc:postgresql://" + endpoint + "/postgres";
                String username = props.getProperty("username");
                String password = props.getProperty("password");

                conn = DriverManager.getConnection(url, username, password);

            } catch (IOException | SQLException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
