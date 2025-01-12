//DatabaseConnectionUtil.java
/*package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnectionUtil {
    private static String url;
    private static String username;
    private static String password;

    static {
        // Load the properties file
        try (InputStream input = DatabaseConnectionUtil.class
                .getResourceAsStream("/config.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Return the connection
        return DriverManager.getConnection(url, username, password);
    }
}
*/
package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnectionUtil {
    private static String url;
    private static String username;
    private static String password;

    static {
        // Load the properties file
        try (InputStream input = DatabaseConnectionUtil.class
                .getResourceAsStream("/config.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Return the connection
        return DriverManager.getConnection(url, username, password);
    }
}
