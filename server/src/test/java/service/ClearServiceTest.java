package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private DataAccess db;
    private ClearService clearService;

    @BeforeEach
    public void setup() {
        db = new MemDataAccess();
        clearService = new ClearService(db);
    }

    @Test
    public void clearSuccessRemovesAll() throws Exception {
        // Add some data
        db.insertUser(new UserData("user1", "pass", "email"));
        db.insertAuth(new AuthData("token1", "user1"));
        db.insertGame(new GameData(1, null, null, "Game1", new chess.ChessGame()));

        // Clear
        clearService.clear();

        // Verify cleared
        assertNull(db.getUser("user1"));
        assertNull(db.getAuth("token1"));
        assertTrue(db.listGames().isEmpty());
    }
}
