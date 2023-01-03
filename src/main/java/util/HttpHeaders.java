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
  final public String path;
  final public String version;
  private static Map<String, String> headers = Maps.newHashMap();

  public HttpHeaders(String headerString) {
    List<String> lines = new ArrayList<>(Arrays.asList(headerString.split("\r?\n")));

    String firstLine = lines.remove(0);

    String[] firstWords = firstLine.split(" ");
    method = firstWords[0];
    path = firstWords[1];
    version = firstWords[2];
    
    Iterator<String> iter = lines.iterator();
    while (iter.hasNext()) {
      String line = iter.next();
      String[] keyVal = line.split(": ?");
      String key = keyVal[0];
      String value = keyVal[1];

      headers.put(key, value);
    }
  }
  
  public void log() {
    log.info("method: " + method);
    log.info("path: " + path);
  }
}
