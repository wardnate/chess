package dataaccess;

import model.*;
import java.util.List;

public interface DataAccess {
    // users
    void clear() throws DataAccessException;

    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    // AuthTokens
    void insertAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    // game
    int insertGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
}
