package service;

import dataaccess.*;
import model.*;
import chess.ChessGame;
import java.util.List;

public class GameService {
    private final DataAccess db;

    public GameService(DataAccess db) {
        this.db = db;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) throw new DataAccessException("Error: unauthorized");
        List<GameData> games = db.listGames();
        return new ListGamesResult(games.toArray(new GameData[0]));
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null)
            throw new DataAccessException("Error: unauthorized");
        if (request.gameName() == null)
            throw new DataAccessException("Error: bad request");

        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0, null, null, request.gameName(), game);
        int gameID = db.insertGame(gameData);

        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null)
            throw new DataAccessException("Error: unauthorized");

        if (request == null || request.gameID() == null || request.playerColor() == null)
            throw new DataAccessException("Error: bad request");
        String color = request.playerColor();
        if (!color.equals("WHITE") && !color.equals("BLACK"))
            throw new DataAccessException("Error: bad request");

        GameData game = db.getGame(request.gameID());
        if (game == null)
            throw new DataAccessException("Error: bad request");

        String username = auth.username();
        ChessGame chessGame = game.game();

        if (color.equals("WHITE")) {
            if (game.whiteUsername() != null && !game.whiteUsername().equals(username))
                throw new DataAccessException("Error: already taken");
            chessGame.setWhitePlayer(username);
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), chessGame);
        } else if (color.equals("BLACK")) {
            if (game.blackUsername() != null && !game.blackUsername().equals(username))
                throw new DataAccessException("Error: already taken");
            chessGame.setBlackPlayer(username);
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), chessGame);
        }
        db.updateGame(game);
    }
}

