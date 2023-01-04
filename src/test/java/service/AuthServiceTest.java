package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import db.DataBase;
import model.User;
import util.HttpRequest;

public class AuthServiceTest {
  @Before
  public void setup() {
    DataBase.clear();
  }

  @Test
  public void signUpTest() {
    String httpString = String.join("\r\n",
        "POST /user/create HTTP/1.1",
        "Host: localhost:8080",
        "Content-Length: 58",
        "Content-Type: application/x-ww-form-urlencoded",
        "Connection: keep-alive",
        "Accept: */*",
        "",
        "userId=test&password=password&name=Tes&email=test@test.com");

    HttpRequest request = new HttpRequest(httpString);

    AuthService authService = new AuthService();

    Map<String, String> form = request.getForm();

    User newUser = new User(
        form.get("userId"),
        form.get("pasword"),
        form.get("name"),
        form.get("email"));

    authService.signUp(newUser);

    User user = DataBase.findUserById("test");
    assertEquals("Tes", user.getName());
  }

  @Test
  public void loginTest() {
    User newUser = new User("test", "test", "test", "test@test.com");
    DataBase.addUser(newUser);

    String httpString = String.join("\r\n",
        "POST /user/login HTTP/1.1",
        "Host: localhost:8080",
        "Content-Length: 25",
        "Content-Type: application/x-ww-form-urlencoded",
        "Connection: keep-alive",
        "Accept: */*",
        "",
        "userId=test&password=test");

    HttpRequest request = new HttpRequest(httpString);

    AuthService authService = new AuthService();

    Map<String, String> form = request.getForm();

    String userId = form.get("userId");
    String password = form.get("password");

    boolean success = authService.login(userId, password);

    assertTrue(success);
  }
}
