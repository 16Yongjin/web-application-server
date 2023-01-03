package util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpRequestTest {
  @Test
  public void parseGetIndexHtml() {
    String httpString = String.join("\r\n",
        "GET /index.html HTTP/1.1",
        "Host: localhost:8080",
        "Connection: keep-alive",
        "Accept: */*");

    HttpRequest headers = new HttpRequest(httpString);

    assertEquals(HttpMethods.GET, headers.method);
    assertEquals("/index.html", headers.path);
    assertEquals("HTTP/1.1", headers.version);
  }

  @Test
  public void parsePostSignUp() {
    String bodyString = "userId=test&password=password&name=Tes&email=test@test.com";
    String httpString = String.join("\n",
        "POST /user/create HTTP/1.1",
        "Host: localhost:8080",
        "Content-Length: 58",
        "Content-Type: application/x-ww-form-urlencoded",
        "Connection: keep-alive",
        "Accept: */*",
        "",
        bodyString);

    HttpRequest request = new HttpRequest(httpString);

    assertEquals(HttpMethods.POST, request.method);
    assertEquals("/user/create", request.path);
    assertEquals("HTTP/1.1", request.version);
    assertEquals(bodyString, request.bodyString);
  }
}
