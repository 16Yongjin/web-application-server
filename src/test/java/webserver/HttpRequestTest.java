package webserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import util.HttpMethod;

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

    assertEquals(HttpMethod.GET, request.getMethod());
    assertEquals("/index.html", request.getPath());
  }

  @Test
  public void parseGetIndexHtmlFile() throws IOException {
    InputStream in = new FileInputStream(new File(testDir + "Http_GET.txt"));
    HttpRequest request = HttpRequest.parseStream(in);

    assertEquals(HttpMethod.GET, request.getMethod());
    assertEquals("/user/create", request.getPath());
    assertEquals("keep-alive", request.getHeader("Connection"));
    assertEquals("javajigi", request.getParameter("userId"));
  }

  @Test
  public void parsePostSignUp() throws IOException {
    String httpString = String.join("\r\n",
        "POST /user/create HTTP/1.1",
        "Host: localhost:8080",
        "Content-Length: 58",
        "Content-Type: application/x-ww-form-urlencoded",
        "Connection: keep-alive",
        "Accept: */*",
        "",
        "userId=test&password=password&name=Tes&email=test@test.com");

    HttpRequest request = HttpRequest.parseString(httpString);

    assertEquals(HttpMethod.POST, request.getMethod());
    assertEquals("/user/create", request.getPath());
    assertEquals("application/x-ww-form-urlencoded", request.getHeader("Content-Type"));
  }

  @Test
  public void parsePostSignUpFile() throws IOException {
    InputStream in = new FileInputStream(new File(testDir + "Http_POST.txt"));
    HttpRequest request = HttpRequest.parseStream(in);

    assertEquals(HttpMethod.POST, request.getMethod());
    assertEquals("/user/create", request.getPath());
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

  @Test
  public void checkLoginTest() throws IOException {
    String httpString = String.join("\r\n",
        "GET /user/list HTTP/1.1",
        "Host: localhost:8080",
        "Cookie: logined=true",
        "Connection: keep-alive",
        "Accept: */*");

    HttpRequest request = HttpRequest.parseString(httpString);

    assertTrue(request.isLogined());
  }
}
