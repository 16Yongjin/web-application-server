package util;

import java.io.BufferedReader;
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
  final public String httpString;
  final public String headerString;
  final public String bodyString;

  private Map<String, String> headers;
  private Map<String, String> queries;

  public static HttpRequest parseStream(InputStream in) throws IOException {
    InputStreamReader reader = new InputStreamReader(in);
    BufferedReader buffer = new BufferedReader(reader);

    String httpString = "";
    String line = buffer.readLine();
    while (!"".equals(line)) {
      httpString += line;
      line = buffer.readLine();

      if (line == null)
        break;
    }

    HttpRequest request = new HttpRequest(httpString);
    return request;
  }

  public HttpRequest(String httpString) {
    this.httpString = httpString;
    int bodyIndex = httpString.indexOf("\r\n\r\n");

    log.info("bodyIndex: " + bodyIndex);

    if (bodyIndex == -1) {
      headerString = httpString;
      bodyString = "";
    } else {
      headerString = httpString.substring(0, bodyIndex);
      bodyString = httpString.substring(bodyIndex + 4);
    }

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
  }

  public String getQuery(String key) {
    return queries.getOrDefault(key, "");
  }

  public String getHeader(String key) {
    return headers.getOrDefault(key, "");
  }

  public Map<String, String> getForm() {
    return HttpRequestUtils.parseQueryString(bodyString);
  }

  public void log() {
    log.info("method: " + method);
    log.info("path: " + path);
    // log.info("headerString: " + httpString);
    // log.info("headerString: " + headerString);
    // log.info("bodyString: " + bodyString);
  }
}
