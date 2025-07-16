package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemDataAccess;
import service.*;

import spark.Spark;

import java.util.Map;

import static spark.Spark.*;

public class Server {

    private final Gson gson = new Gson();

    private DataAccess dataAccess;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    public int run(int desiredPort) {
        port(desiredPort);

        staticFiles.location("web");

        // Initialize data access and services
        dataAccess = new MemDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        clearService = new ClearService(dataAccess);

        // 1. Clear application - DELETE /db
        delete("/db", (req, res) -> {
            try {
                clearService.clear();
                res.status(200);
                return "{}";
            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // 2. Register - POST /user
        post("/user", (req, res) -> {
            try {
                var registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
                var result = userService.register(registerRequest);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                if (e.getMessage().contains("already taken")) {
                    res.status(403);
                } else if (e.getMessage().contains("bad request")) {
                    res.status(400);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // 3. Login - POST /session
        post("/session", (req, res) -> {
            try {
                var loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                var result = userService.login(loginRequest);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                if (e.getMessage().contains("bad request")) {
                    res.status(400);
                } else if (e.getMessage().contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // 4. Logout - DELETE /session
        delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                if (authToken == null) throw new DataAccessException("unauthorized");
                userService.logout(authToken);
                res.status(200);
                return "{}";
            } catch (DataAccessException e) {
                if (e.getMessage().contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // 5. List Games - GET /game
        get("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                if (authToken == null) throw new DataAccessException("unauthorized");
                var result = gameService.listGames(authToken);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                if (e.getMessage().contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // 6. Create Game - POST /game
        post("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                if (authToken == null) throw new DataAccessException("unauthorized");
                var createRequest = gson.fromJson(req.body(), CreateGameRequest.class);
                var result = gameService.createGame(createRequest, authToken);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                if (e.getMessage().contains("unauthorized")) {
                    res.status(401);
                } else if (e.getMessage().contains("bad request")) {
                    res.status(400);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // 7. Join Game - PUT /game
        put("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                if (authToken == null) throw new DataAccessException("unauthorized");
                var joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
                gameService.joinGame(joinRequest, authToken);
                res.status(200);
                return "{}";
            } catch (DataAccessException e) {
                if (e.getMessage().contains("unauthorized")) {
                    res.status(401);
                } else if (e.getMessage().contains("already taken")) {
                    res.status(403);
                } else if (e.getMessage().contains("bad request")) {
                    res.status(400);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        init();
        awaitInitialization();
        return port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
