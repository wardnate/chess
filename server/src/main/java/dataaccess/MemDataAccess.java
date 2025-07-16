package dataaccess;

import model.*;
import java.util.*;

public class MemDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameIdSeq = 1;

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
        gameIdSeq = 1;
    }

    // ==== User Operations ====
    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) throw new DataAccessException("User or username required");
        if (users.containsKey(user.username())) throw new DataAccessException("User already exists");
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) throw new DataAccessException("Username required");
        return users.get(username);
    }

    // ==== Auth Operations ====
    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.authToken() == null) throw new DataAccessException("Auth token required");
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) throw new DataAccessException("Auth token required");
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null || !auths.containsKey(authToken)) throw new DataAccessException("Auth token not found");
        auths.remove(authToken);
    }

    // ==== Game Operations ====
    @Override
    public int insertGame(GameData game) throws DataAccessException {
        if (game == null) throw new DataAccessException("Game required");
        int id = gameIdSeq++;
        GameData newGame = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(id, newGame);
        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (game == null) throw new DataAccessException("Game required");
        int id = game.gameID();
        if (!games.containsKey(id)) throw new DataAccessException("Game not found");
        games.put(id, game);
    }
}

