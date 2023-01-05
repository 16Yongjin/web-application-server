package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import db.DataBase;
import model.User;
import webserver.HttpRequest;

public class UserSeriveTest {
  @Before
  public void setup() {
    DataBase.clear();
  }

  @Test
  public void listUserTest() {
    try {
      String httpString = String.join("\r\n",
          "GET /user/list HTTP/1.1",
          "Host: localhost:8080",
          "Cookie: logined=true",
          "Connection: keep-alive",
          "Accept: */*");

      HttpRequest request = HttpRequest.parseString(httpString);

      User user1 = new User("user1", "password", "user1", "user1@test.com");
      User user2 = new User("user2", "password", "user2", "user2@test.com");
      User user3 = new User("user3", "password", "user3", "user3@test.com");

      DataBase.addUser(user1);
      DataBase.addUser(user2);
      DataBase.addUser(user3);

      UserService userService = new UserService();

      userService.list();
    } catch (IOException e) {
    }
  }
}
