package webserver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import util.HttpMethod;

public class RequestLineTest {
  @Test
  public void parse_get_index_html() throws IOException {
    RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
    assertEquals(HttpMethod.GET, line.getMethod());
    assertEquals("/index.html", line.getPath());
  }

  @Test
  public void parse_post_index_html() throws IOException {
    RequestLine line = new RequestLine("POST /index.html HTTP/1.1");
    assertEquals(HttpMethod.POST, line.getMethod());
    assertEquals("/index.html", line.getPath());
  }

  @Test
  public void parse_get_user_with_queries() throws IOException {
    RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=password HTTP/1.1");
    assertEquals(HttpMethod.GET, line.getMethod());
    assertEquals("/user/create", line.getPath());
    Map<String, String> params = line.getParams();
    assertEquals(2, params.size());
  }
}
