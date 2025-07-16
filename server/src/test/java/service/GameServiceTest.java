package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private DataAccess db;
    private GameService gameService;
    private UserService userService;

    private String authToken;

    @BeforeEach
    public void setup() throws Exception {
        db = new MemDataAccess();
        userService = new UserService(db);
        gameService = new GameService(db);

        // register user, get auth token
        RegisterRequest regReq = new RegisterRequest("user1", "pass1", "user1@example.com");
        RegisterResult regRes = userService.register(regReq);
        authToken = regRes.authToken();
    }

    // create game success
    @Test
    public void createGameSuccess() throws Exception {
        CreateGameRequest createReq = new CreateGameRequest("Test Game");
        CreateGameResult createRes = gameService.createGame(createReq, authToken);

        assertTrue(createRes.gameID() > 0);
    }

    // bad create game request
    @Test
    public void createGameBadRequest() {
        CreateGameRequest badCreate = new CreateGameRequest(null);

        DataAccessException e = assertThrows(DataAccessException.class,
                () -> gameService.createGame(badCreate, authToken));
        assertTrue(e.getMessage().toLowerCase().contains("bad request"));
    }

    // create game unauthorized
    @Test
    public void createGameUnauthorized() {
        CreateGameRequest req = new CreateGameRequest("Game");
        DataAccessException e = assertThrows(DataAccessException.class,
                () -> gameService.createGame(req, "badtoken"));
        assertTrue(e.getMessage().toLowerCase().contains("unauthorized"));
    }

    // list games return
    @Test
    public void listGamesEmpty() throws Exception {
        ListGamesResult result = gameService.listGames(authToken);
        assertNotNull(result.games());
        assertEquals(0, result.games().length);
    }

    // list game return
    @Test
    public void listGamesSuccess() throws Exception {
        gameService.createGame(new CreateGameRequest("Game1"), authToken);
        gameService.createGame(new CreateGameRequest("Game2"), authToken);

        ListGamesResult listRes = gameService.listGames(authToken);
        assertEquals(2, listRes.games().length);
    }

    // join as white
    @Test
    public void joinGameSuccessWhite() throws Exception {
        CreateGameResult createRes = gameService.createGame(new CreateGameRequest("Test Game"), authToken);

        JoinGameRequest joinReq = new JoinGameRequest("WHITE", createRes.gameID());
        assertDoesNotThrow(() -> gameService.joinGame(joinReq, authToken));

        GameData game = db.getGame(createRes.gameID());
        assertEquals("user1", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    // join as black
    @Test
    public void joinGameSuccessBlack() throws Exception {
        CreateGameResult createRes = gameService.createGame(new CreateGameRequest("Test Game"), authToken);

        JoinGameRequest joinReq = new JoinGameRequest("BLACK", createRes.gameID());
        assertDoesNotThrow(() -> gameService.joinGame(joinReq, authToken));

        GameData game = db.getGame(createRes.gameID());
        assertEquals("user1", game.blackUsername());
        assertNull(game.whiteUsername());
    }

    // join game unauthorized
    @Test
    public void joinGameUnauthorized() {
        JoinGameRequest joinReq = new JoinGameRequest("WHITE", 1);
        DataAccessException e = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(joinReq, "badtoken"));
        assertTrue(e.getMessage().contains("unauthorized"));
    }

    // null game ID
    @Test
    public void joinGameBadRequestNullGameID() {
        JoinGameRequest joinReq = new JoinGameRequest("WHITE", null);
        DataAccessException e = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(joinReq, authToken));
        assertTrue(e.getMessage().toLowerCase().contains("bad request"));
    }

    // invalid color
    @Test
    public void joinGameBadRequestInvalidColor() throws Exception {
        CreateGameResult createRes = gameService.createGame(new CreateGameRequest("Test Game"), authToken);
        JoinGameRequest joinReq = new JoinGameRequest("GREEN", createRes.gameID());

        DataAccessException e = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(joinReq, authToken));
        assertTrue(e.getMessage().toLowerCase().contains("bad request"));
    }

    // game color already taken
    @Test
    public void joinGameColorAlreadyTaken() throws Exception {
        CreateGameResult createRes = gameService.createGame(new CreateGameRequest("Test Game"), authToken);

        JoinGameRequest joinReqWhite = new JoinGameRequest("WHITE", createRes.gameID());
        gameService.joinGame(joinReqWhite, authToken);

        // Register a second user
        RegisterRequest user2Reg = new RegisterRequest("user2", "pass2", "u2@example.com");
        RegisterResult user2Res = userService.register(user2Reg);

        // Second user tries to join white again
        JoinGameRequest badJoin = new JoinGameRequest("WHITE", createRes.gameID());
        DataAccessException e = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(badJoin, user2Res.authToken()));
        assertTrue(e.getMessage().toLowerCase().contains("already taken"));
    }
}
