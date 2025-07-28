package dataaccess;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }
    public static void createTables() throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.createStatement()) {

            // User Table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS user (" +
                            "username VARCHAR(255) PRIMARY KEY," +
                            "password VARCHAR(255) NOT NULL," +
                            "email VARCHAR(255) NOT NULL" +
                            ");"
            );

            // Auth Table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS auth (" +
                            "authToken VARCHAR(255) PRIMARY KEY," +
                            "username VARCHAR(255) NOT NULL," +
                            "FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE" +
                            ");"
            );

            // Game Table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS game (" +
                            "gameID INT AUTO_INCREMENT PRIMARY KEY," +
                            "whiteUsername VARCHAR(255)," +
                            "blackUsername VARCHAR(255)," +
                            "gameName VARCHAR(255) NOT NULL," +
                            "gameJson TEXT," +
                            "FOREIGN KEY (whiteUsername) REFERENCES user(username) ON DELETE SET NULL," +
                            "FOREIGN KEY (blackUsername) REFERENCES user(username) ON DELETE SET NULL" +
                            ");"
            );

        } catch (SQLException ex) {
            throw new DataAccessException("failed to create tables", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
