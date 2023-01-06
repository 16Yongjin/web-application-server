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

import util.HttpMethods;
import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
  private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

  private String method;
  private String path;
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

      processRequestLine(line);

      line = buffer.readLine();
      while (!line.equals("")) {
        log.debug("headers : {}", line);
        String[] tokens = line.split(":");
        headers.put(tokens[0].trim(), tokens[1].trim());
        line = buffer.readLine();

        if (line == null)
          break;
      }

      if (HttpMethods.POST.equals(method)) {
        String body = IOUtils.readData(buffer, Integer.parseInt(headers.get("Content-Length")));
        params = HttpRequestUtils.parseQueryString(body);
      }
    } catch (IOException io) {
      log.error(io.getMessage());
    }
  }

  private void processRequestLine(String requestLine) {
    log.debug("request line : {}", requestLine);
    String[] tokens = requestLine.split(" ");
    method = tokens[0];

    if (HttpMethods.POST.equals(method)) {
      path = tokens[1];
      return;
    }

    int queryIndex = tokens[1].indexOf("?");
    if (queryIndex == -1) {
      path = tokens[1];
    } else {
      path = tokens[1].substring(0, queryIndex);
      params = HttpRequestUtils.parseQueryString(tokens[1].substring(queryIndex + 1));
    }
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getHeader(String key) {
    return headers.get(key);
  }

  public String getParameter(String key) {
    return params.get(key);
  }

  public Map<String, String> getCookies() {
    return HttpRequestUtils.parseCookies(headers.get("Cookie"));
  }

  public void log() {
    log.info("method: " + method);
    log.info("path: " + path);
  }
}
