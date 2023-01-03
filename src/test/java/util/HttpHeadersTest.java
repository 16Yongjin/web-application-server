package util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpHeadersTest {
  @Test
  public void parseHttpHeadersIndexHtml() {
      String httpHeaderString = String.join("\r\n", 
        "GET /index.html HTTP/1.1",
        "Host: localhost:8080",
        "Connection: keep-alive",
        "Accept: */*"      
      );

      HttpHeaders headers = new HttpHeaders(httpHeaderString);

      assertEquals(HttpMethods.GET, headers.method);
      assertEquals("/index.html", headers.path);
      assertEquals("HTTP/1.1", headers.version);
  }
}
