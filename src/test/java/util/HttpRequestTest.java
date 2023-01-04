package util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class HttpRequestTest {
  @Test
  public void parseGetIndexHtml() {
    try {
      String httpString = String.join("\r\n",
          "GET /index.html HTTP/1.1",
          "Host: localhost:8080",
          "Connection: keep-alive",
          "Accept: */*");

      HttpRequest request = HttpRequest.parseString(httpString);

      assertEquals(HttpMethods.GET, request.method);
      assertEquals("/index.html", request.path);
      assertEquals("HTTP/1.1", request.version);
    } catch (IOException e) {
    }
  }

  @Test
  public void parsePostSignUp() {
    try {
      String bodyString = "userId=test&password=password&name=Tes&email=test@test.com";
      String httpString = String.join("\r\n",
          "POST /user/create HTTP/1.1",
          "Host: localhost:8080",
          "Content-Length: 58",
          "Content-Type: application/x-ww-form-urlencoded",
          "Connection: keep-alive",
          "Accept: */*",
          "",
          bodyString);

      HttpRequest request = HttpRequest.parseString(httpString);

      assertEquals(HttpMethods.POST, request.method);
      assertEquals("/user/create", request.path);
      assertEquals("HTTP/1.1", request.version);
      assertEquals("application/x-ww-form-urlencoded", request.getHeader("Content-Type"));
      assertEquals(bodyString, request.bodyString);
    } catch (IOException e) {
    }
  }

  @Test
  public void getCookieTest() {
    try {
      String httpString = String.join("\r\n",
          "GET /user/list HTTP/1.1",
          "Host: localhost:8080",
          "Cookie: logined=true",
          "Connection: keep-alive",
          "Accept: */*");

      HttpRequest request = HttpRequest.parseString(httpString);

      assertEquals("logined=true", request.getHeader("Cookie"));
    } catch (IOException e) {
    }
  }
}
