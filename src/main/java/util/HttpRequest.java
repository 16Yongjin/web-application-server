package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class HttpRequest {
  private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

  final public String method;
  final public String fullPath;
  final public String path;
  final public String version;
  final public String headerString;
  final public String bodyString;

  private Map<String, String> headers;
  private Map<String, String> queries;
  private Map<String, String> form;

  public static HttpRequest parseString(String httpString) throws IOException {
    InputStream stream = new ByteArrayInputStream(httpString.getBytes());
    return parseStream(stream);
  }

  public static HttpRequest parseStream(InputStream in) throws IOException {
    BufferedReader buffer = new BufferedReader(new InputStreamReader(in, "UTF-8"));

    String headerString = "";
    String line = buffer.readLine();
    int contentLength = 0;
    while (!"".equals(line)) {
      headerString += line + "\n";

      if (line.contains("Content-Length")) {
        contentLength = getContentLength(line);
      }
      line = buffer.readLine();

      if (line == null)
        break;
    }

    String bodyString = IOUtils.readData(buffer, contentLength);

    HttpRequest request = new HttpRequest(headerString, bodyString);
    return request;
  }

  public HttpRequest(String headerString, String bodyString) {
    this.headerString = headerString;
    this.bodyString = bodyString;

    List<String> lines = new ArrayList<>(Arrays.asList(headerString.split("\r?\n")));

    String firstLine = lines.remove(0);

    String[] firstWords = firstLine.split(" ");
    method = firstWords[0];
    fullPath = firstWords[1];
    version = firstWords[2];

    int queryIndex = fullPath.indexOf("?");
    if (queryIndex == -1) {
      path = fullPath;
      queries = Maps.newHashMap();
    } else {
      path = fullPath.substring(0, queryIndex);
      String queryString = fullPath.substring(queryIndex + 1);
      queries = HttpRequestUtils.parseQueryString(queryString);
    }

    headers = Maps.newHashMap();
    Iterator<String> iter = lines.iterator();
    while (iter.hasNext()) {
      String line = iter.next();
      String[] keyVal = line.split(": ");
      String key = keyVal[0];
      String value = keyVal[1];

      headers.put(key, value);
    }

    form = HttpRequestUtils.parseQueryString(bodyString);
  }

  public String getQuery(String key) {
    return queries.getOrDefault(key, "");
  }

  public String getHeader(String key) {
    return headers.getOrDefault(key, "");
  }

  public Map<String, String> getCookie() {
    return HttpRequestUtils.parseCookies(getHeader("Cookie"));
  }

  public Map<String, String> getForm() {
    return form;
  }

  public String getForm(String key) {
    return form.getOrDefault(key, "");
  }

  public String getParameter(String key) {
    if (method.equals(HttpMethods.GET))
      return getQuery(key);
    if (method.equals(HttpMethods.POST))
      return getForm(key);

    return "";
  }

  private static int getContentLength(String line) {
    String[] headerTokens = line.split(":");
    return Integer.parseInt(headerTokens[1].trim());
  }

  public void log() {
    log.info("method: " + method);
    log.info("path: " + path);
  }
}
