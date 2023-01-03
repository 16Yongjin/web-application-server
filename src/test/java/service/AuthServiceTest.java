package service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import db.DataBase;
import model.User;
import util.HttpHeaders;

public class AuthServiceTest {
  @Test
  public void parseHttpHeadersIndexHtml() {
    String httpHeaderString = String.join("\r\n",
        "GET /user/create?userId=test&password=password&name=Tes&email=test@test.com HTTP/1.1",
        "Host: localhost:8080",
        "Connection: keep-alive",
        "Accept: */*");

    HttpHeaders headers = new HttpHeaders(httpHeaderString);

    AuthService authService = new AuthService();

    User newUser = new User(
        headers.getQuery("userId"),
        headers.getQuery("pasword"),
        headers.getQuery("name"),
        headers.getQuery("email"));

    authService.signUp(newUser);

    User user = DataBase.findUserById("test");
    assertEquals("Tes", user.getName());
  }
}
