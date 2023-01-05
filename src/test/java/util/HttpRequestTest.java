package util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class HttpRequestTest {
  private String testDir = "./src/test/resources/";

  @Test
  public void parseGetIndexHtml() throws IOException {
    String httpString = String.join("\r\n",
        "GET /index.html HTTP/1.1",
        "Host: localhost:8080",
        "Connection: keep-alive",
        "Accept: */*");

    HttpRequest request = HttpRequest.parseString(httpString);

    assertEquals(HttpMethods.GET, request.method);
    assertEquals("/index.html", request.path);
    assertEquals("HTTP/1.1", request.version);
  }

  @Test
  public void parseGetIndexHtmlFile() throws IOException {
    InputStream in = new FileInputStream(new File(testDir + "Http_GET.txt"));
    HttpRequest request = HttpRequest.parseStream(in);

    assertEquals(HttpMethods.GET, request.method);
    assertEquals("/user/create", request.path);
    assertEquals("HTTP/1.1", request.version);
    assertEquals("keep-alive", request.getHeader("Connection"));
    assertEquals("javajigi", request.getParameter("userId"));
  }

  @Test
  public void parsePostSignUp() throws IOException {
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
  }

  @Test
  public void parsePostSignUpFile() throws IOException {
    InputStream in = new FileInputStream(new File(testDir + "Http_POST.txt"));
    HttpRequest request = HttpRequest.parseStream(in);

    assertEquals(HttpMethods.POST, request.method);
    assertEquals("/user/create", request.path);
    assertEquals("HTTP/1.1", request.version);
    assertEquals("keep-alive", request.getHeader("Connection"));
    assertEquals("javajigi", request.getParameter("userId"));
  }

  @Test
  public void getCookieTest() throws IOException {
    String httpString = String.join("\r\n",
        "GET /user/list HTTP/1.1",
        "Host: localhost:8080",
        "Cookie: logined=true",
        "Connection: keep-alive",
        "Accept: */*");

    HttpRequest request = HttpRequest.parseString(httpString);

    assertEquals("logined=true", request.getHeader("Cookie"));
  }
}
