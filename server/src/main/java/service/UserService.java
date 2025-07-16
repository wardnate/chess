package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private final DataAccess db;

    public UserService(DataAccess db) {
        this.db = db;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        // Validate input
        if (request.username() == null || request.password() == null || request.email() == null)
            throw new DataAccessException("Error: bad request");
        if (db.getUser(request.username()) != null)
            throw new DataAccessException("Error: already taken");

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        db.insertUser(newUser);

        String authToken = UUID.randomUUID().toString();
        db.insertAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null)
            throw new DataAccessException("Error: bad request");
        UserData user = db.getUser(request.username());
        if (user == null || !user.password().equals(request.password()))
            throw new DataAccessException("Error: unauthorized");

        String authToken = UUID.randomUUID().toString();
        db.insertAuth(new AuthData(authToken, request.username()));
        return new LoginResult(request.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null)
            throw new DataAccessException("Error: unauthorized");
        db.deleteAuth(authToken);
    }
}

