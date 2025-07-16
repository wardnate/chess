package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private DataAccess db;
    private UserService userService;

    @BeforeEach
    public void setup() {
        db = new MemDataAccess();
        userService = new UserService(db);
    }

    // register new user
    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("user1", "pass1", "user1@example.com");
        RegisterResult result = userService.register(request);
        assertEquals(request.username(), result.username());
        assertNotNull(result.authToken());
    }

    // missing fields
    @Test
    public void registerBadRequest() {
        RegisterRequest request = new RegisterRequest(null, "pass", "email@example.com");
        Exception e = assertThrows(DataAccessException.class, () -> userService.register(request));
        assertTrue(e.getMessage().toLowerCase().contains("bad request"));
    }

    // existing user
    @Test
    public void registerUserAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("user1", "pass1", "user1@example.com");
        userService.register(request);

        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.register(request));
        assertTrue(e.getMessage().toLowerCase().contains("already taken"));
    }

    // login!
    @Test
    public void loginSuccess() throws Exception {
        RegisterRequest registerReq = new RegisterRequest("user1", "pass1", "user1@example.com");
        userService.register(registerReq);

        LoginRequest loginReq = new LoginRequest("user1", "pass1");
        LoginResult loginResult = userService.login(loginReq);
        assertEquals("user1", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    // login bad credentials
    @Test
    public void loginUnauthorized() throws Exception {
        RegisterRequest registerReq = new RegisterRequest("user1", "pass1", "user1@example.com");
        userService.register(registerReq);

        LoginRequest loginReq = new LoginRequest("user1", "wrongpass");
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.login(loginReq));
        assertTrue(e.getMessage().toLowerCase().contains("unauthorized"));
    }

    // missing username
    @Test
    public void loginBadRequest() {
        LoginRequest badRequest = new LoginRequest(null, "pass1");
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.login(badRequest));
        assertTrue(e.getMessage().toLowerCase().contains("bad request"));
    }

    // logout
    @Test
    public void logoutSuccess() throws Exception {
        RegisterRequest reg = new RegisterRequest("user1", "pass1", "user1@example.com");
        RegisterResult regResult = userService.register(reg);
        assertNotNull(regResult.authToken());

        assertDoesNotThrow(() -> userService.logout(regResult.authToken()));
    }

    // logout fail
    @Test
    public void logoutUnauthorized() {
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.logout("badtoken"));
        assertTrue(e.getMessage().toLowerCase().contains("unauthorized"));
    }
}
