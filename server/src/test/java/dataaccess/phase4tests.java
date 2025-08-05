package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;

import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;

public class phase4tests {
    private static MySqlDataAccess dao;

    @BeforeAll
    public static void setupOnce() throws DataAccessException {
        dao = new MySqlDataAccess();
    }

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        dao.clear();
    }

    // --- clear() +test

    @Test
    public void testClear() {
        assertDoesNotThrow(() -> dao.clear());
    }

    // --- insertUser +/- tests

    @Test
    public void testInsertUserPositive() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@example.com");
        dao.insertUser(user);

        UserData retrieved = dao.getUser("alice");
        assertNotNull(retrieved);
        assertEquals("alice", retrieved.username());
        assertEquals("alice@example.com", retrieved.email());
        assertNotEquals("password123", retrieved.password()); // Password should be hashed
        assertTrue(retrieved.password().startsWith("$2a$") || retrieved.password().startsWith("$2b$")); // BCrypt hash prefix
    }

    @Test
    public void testInsertUserNegativeDuplicate() throws DataAccessException {
        UserData user = new UserData("bob", "secret", "bob@example.com");
        dao.insertUser(user);

        UserData duplicateUser = new UserData("bob", "secret2", "bob2@example.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.insertUser(duplicateUser));
        assertTrue(ex.getMessage().toLowerCase().contains("already exists"));
    }

    @Test
    public void testInsertUserNegativeNullUser() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.insertUser(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testInsertUserNegativeNullUsername() {
        UserData user = new UserData(null, "pw", "email@example.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.insertUser(user));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    // --- getUser +/- tests

    @Test
    public void testGetUserPositive() throws DataAccessException {
        UserData user = new UserData("carol", "pw", "carol@example.com");
        dao.insertUser(user);

        UserData retrieved = dao.getUser("carol");
        assertNotNull(retrieved);
        assertEquals("carol", retrieved.username());
        assertEquals("carol@example.com", retrieved.email());
    }

    @Test
    public void testGetUserNegativeUsernameNull() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.getUser(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testGetUserNegativeUserNotFound() throws DataAccessException {
        UserData retrieved = dao.getUser("nonexistent");
        assertNull(retrieved);
    }

    // insertAuth +/- tests

    @Test
    public void testInsertAuthPositive() throws DataAccessException {
        UserData user = new UserData("dave", "pw", "dave@example.com");
        dao.insertUser(user);

        AuthData auth = new AuthData("token123", "dave");
        dao.insertAuth(auth);

        AuthData retrieved = dao.getAuth("token123");
        assertNotNull(retrieved);
        assertEquals("token123", retrieved.authToken());
        assertEquals("dave", retrieved.username());
    }

    @Test
    public void testInsertAuthNegativeNullAuth() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.insertAuth(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testInsertAuthNegativeNullAuthToken() {
        AuthData auth = new AuthData(null, "someone");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.insertAuth(auth));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    // getAuth +/- tests

    @Test
    public void testGetAuthPositive() throws DataAccessException {
        UserData user = new UserData("erin", "pw", "erin@example.com");
        dao.insertUser(user);
        AuthData auth = new AuthData("token456", "erin");
        dao.insertAuth(auth);

        AuthData retrieved = dao.getAuth("token456");
        assertNotNull(retrieved);
        assertEquals("token456", retrieved.authToken());
        assertEquals("erin", retrieved.username());
    }

    @Test
    public void testGetAuthNegativeNull() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.getAuth(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testGetAuthNegativeNotFound() throws DataAccessException {
        AuthData retrieved = dao.getAuth("nonexistentToken");
        assertNull(retrieved);
    }

    // deleteAuth +/- tests

    @Test
    public void testDeleteAuthPositive() throws DataAccessException {
        UserData user = new UserData("frank", "pw", "frank@example.com");
        dao.insertUser(user);
        AuthData auth = new AuthData("token789", "frank");
        dao.insertAuth(auth);

        dao.deleteAuth("token789");
        AuthData deleted = dao.getAuth("token789");
        assertNull(deleted);
    }

    @Test
    public void testDeleteAuthNegativeNull() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.deleteAuth(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testDeleteAuthNegativeNotFound() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.deleteAuth("notExist"));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    // -insertGame +/- tests

    @Test
    public void testInsertGamePositive() throws DataAccessException {
        // Add users before referencing them in a game
        dao.insertUser(new UserData("alice", "password", "alice@example.com"));
        dao.insertUser(new UserData("bob", "password", "bob@example.com"));

        ChessGame emptyGame = new ChessGame();

        GameData game = new GameData(0, "alice", "bob", "Friendly Game", emptyGame);
        int id = dao.insertGame(game);
        assertTrue(id > 0);

        GameData retrieved = dao.getGame(id);
        assertNotNull(retrieved);
        assertEquals("Friendly Game", retrieved.gameName());
        assertEquals("alice", retrieved.whiteUsername());
        assertEquals("bob", retrieved.blackUsername());
        assertNotNull(retrieved.game());
    }

    @Test
    public void testInsertGameNegativeNull() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.insertGame(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    // getGame +/- tests

    @Test
    public void testGetGamePositive() throws DataAccessException {
        dao.insertUser(new UserData("charlie", "password", "charlie@example.com"));
        dao.insertUser(new UserData("dan", "password", "dan@example.com"));

        ChessGame emptyGame = new ChessGame();
        GameData game = new GameData(0, "charlie", "dan", "Match 1", emptyGame);
        int id = dao.insertGame(game);

        GameData retrieved = dao.getGame(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.gameID());
    }

    @Test
    public void testGetGameNegativeNoMatch() throws DataAccessException {
        GameData retrieved = dao.getGame(-99);
        assertNull(retrieved);
    }

    // listGames + tests

    @Test
    public void testListGamesPositive() throws DataAccessException {
        dao.insertUser(new UserData("eve", "password", "eve@example.com"));
        dao.insertUser(new UserData("frank", "password", "frank@example.com"));

        GameData g1 = new GameData(0, "eve", null, "Game1", new ChessGame());
        dao.insertGame(g1);
        GameData g2 = new GameData(0, "eve", "frank", "Game2", new ChessGame());
        dao.insertGame(g2);

        List<GameData> games = dao.listGames();
        assertNotNull(games);
        assertTrue(games.size() >= 2);
    }

    // updateGame +/- tests

    @Test
    public void testUpdateGamePositive() throws DataAccessException {
        dao.insertUser(new UserData("gina", "password", "gina@example.com"));
        dao.insertUser(new UserData("harry", "password", "harry@example.com"));

        GameData g = new GameData(0, "gina", "harry", "Initial Game", new ChessGame());
        int id = dao.insertGame(g);

        // Modify some fields and update
        ChessGame updatedGame = new ChessGame(); // could be any modification
        GameData updatedData = new GameData(id, "gina", "harry", "Updated Game", updatedGame);

        assertDoesNotThrow(() -> dao.updateGame(updatedData));

        GameData retrieved = dao.getGame(id);
        assertEquals("Updated Game", retrieved.gameName());
    }

    @Test
    public void testUpdateGameNegativeNull() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.updateGame(null));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testUpdateGameNegativeNoSuchGame() {
        ChessGame game = new ChessGame();
        GameData nonExistent = new GameData(-1000, "unknown", "unknown", "No Game", game);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> dao.updateGame(nonExistent));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }
}
