package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class HttpHeaders {
  private static final Logger log = LoggerFactory.getLogger(HttpHeaders.class);

  final public String method;
  final public String fullPath;
  final public String path;
  final public String version;

  private Map<String, String> headers;
  private Map<String, String> queries;

  public HttpHeaders(String headerString) {
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
      String[] keyVal = line.split(": ?");
      String key = keyVal[0];
      String value = keyVal[1];

      headers.put(key, value);
    }
  }

  public String getQuery(String key) {
    return queries.getOrDefault(key, "");
  }

  public void log() {
    log.info("method: " + method);
    log.info("path: " + path);
  }
}
