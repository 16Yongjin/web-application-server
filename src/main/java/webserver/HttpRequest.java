package webserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpMethod;
import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
  private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

  private RequestLine requestLine;
  private Map<String, String> headers = new HashMap<>();
  private Map<String, String> params = new HashMap<>();

  public static HttpRequest parseString(String httpString) throws IOException {
    InputStream stream = new ByteArrayInputStream(httpString.getBytes());
    return parseStream(stream);
  }

  public static HttpRequest parseStream(InputStream in) throws IOException {
    return new HttpRequest(in);
  }

  public HttpRequest(InputStream in) {
    try {

      BufferedReader buffer = new BufferedReader(new InputStreamReader(in, "UTF-8"));

      String line = buffer.readLine();
      if (line == null) {
        return;
      }

      requestLine = new RequestLine(line);

      line = buffer.readLine();
      while (!line.equals("")) {
        log.debug("headers : {}", line);
        String[] tokens = line.split(":");
        headers.put(tokens[0].trim(), tokens[1].trim());
        line = buffer.readLine();

        if (line == null)
          break;
      }

      if (requestLine.getMethod().isPost()) {
        String body = IOUtils.readData(buffer, Integer.parseInt(headers.get("Content-Length")));
        params = HttpRequestUtils.parseQueryString(body);
      } else {
        params = requestLine.getParams();
      }
    } catch (IOException io) {
      log.error(io.getMessage());
    }
  }

  public HttpMethod getMethod() {
    return requestLine.getMethod();
  }

  public String getPath() {
    return requestLine.getPath();
  }

  public String getHeader(String key) {
    return headers.get(key);
  }

  public String getParameter(String key) {
    return params.get(key);
  }

  public boolean isLogined() {
    return getCookies().getOrDefault("logined", "false").equals("true");
  }

  public Map<String, String> getCookies() {
    return HttpRequestUtils.parseCookies(headers.get("Cookie"));
  }

  public void log() {
    log.info("method: " + getMethod());
    log.info("path: " + getPath());
  }
}
