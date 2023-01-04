package util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class LoginCheckerTest {
  @Test
  public void checkLoginTest() {
    try {
      String httpString = String.join("\r\n",
          "GET /user/list HTTP/1.1",
          "Host: localhost:8080",
          "Cookie: logined=true",
          "Connection: keep-alive",
          "Accept: */*");

      HttpRequest request = HttpRequest.parseString(httpString);

      assertTrue(LoginChecker.isLogined(request));
    } catch (IOException e) {
    }
  }
}
