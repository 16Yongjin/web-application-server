package util;

import webserver.HttpRequest;

public class LoginChecker {
  public static boolean isLogined(HttpRequest request) {
    return request.getCookie().getOrDefault("logined", "false").equals("true");
  }
}
