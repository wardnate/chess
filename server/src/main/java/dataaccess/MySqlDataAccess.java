package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;

public class MySqlDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    // Constructor ensures database and tables are created once at startup
    public MySqlDataAccess() throws DataAccessException {
        // Create the database if it does not exist
        DatabaseManager.createDatabase();
        // Create necessary tables if they do not exist
        DatabaseManager.createTables();

    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth;");
            stmt.executeUpdate("DELETE FROM game;");
            stmt.executeUpdate("DELETE FROM user;");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear database tables", e);
        }
    }

    // User methods

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) {
            throw new DataAccessException("User or username cannot be null");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("duplicate")) {
                throw new DataAccessException("User already exists", e);
            }
            throw new DataAccessException("Failed to insert user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Username cannot be null");
        }
        String sql = "SELECT username, password, email FROM user WHERE username = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String uname = rs.getString("username");
                    String pass = rs.getString("password"); // bcrypt hashed password
                    String email = rs.getString("email");
                    return new UserData(uname, pass, email);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve user", e);
        }
    }

    // Auth methods

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.authToken() == null) {
            throw new DataAccessException("Auth token cannot be null");
        }
        final String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert auth token", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Auth token cannot be null");
        }
        final String sql = "SELECT authToken, username FROM auth WHERE authToken = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve auth token", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Auth token cannot be null");
        }
        final String sql = "DELETE FROM auth WHERE authToken = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Auth token not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token", e);
        }
    }

    // Game methods

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("Game cannot be null");
        }
        String gameJson = gson.toJson(game.game());
        final String sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameJson) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gameJson);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                } else {
                    throw new DataAccessException("Failed to retrieve generated game ID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        final String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game WHERE gameID = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    String name = rs.getString("gameName");
                    String json = rs.getString("gameJson");
                    ChessGame game = gson.fromJson(json, ChessGame.class);
                    return new GameData(gameID, white, black, name, game);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        final String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game;";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String white = rs.getString("whiteUsername");
                String black = rs.getString("blackUsername");
                String name = rs.getString("gameName");
                String json = rs.getString("gameJson");
                ChessGame game = gson.fromJson(json, ChessGame.class);
                games.add(new GameData(gameID, white, black, name, game));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("Game cannot be null");
        }
        String gameJson = gson.toJson(game.game());
        final String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJson = ? WHERE gameID = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gameJson);
            stmt.setInt(5, game.gameID());
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Game not found for update");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }
}
