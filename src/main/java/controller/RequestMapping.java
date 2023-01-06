package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
  private static Map<String, Controller> controllers = new HashMap<>();

  static {
    controllers.put("/user/create", new CreateUserController());
    controllers.put("/user/list", new ListUserController());
    controllers.put("/user/login", new LoginController());
    controllers.put("DEFAULT", new DefaultController());
  }

  public static Controller getController(String requestUrl) {
    if (controllers.containsKey(requestUrl)) {
      return controllers.get(requestUrl);
    } else {
      return controllers.get("DEFAULT");
    }
  }
}
