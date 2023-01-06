package controller;

import java.io.IOException;
import java.util.Collection;

import model.User;
import service.UserService;
import util.LoginChecker;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class ListUserController extends AbstractController {
  @Override
  public void doGet(HttpRequest request, HttpResponse response) {
    try {
      boolean logined = LoginChecker.isLogined(request);

      if (!logined) {
        response.forward("/user/login.html");
        return;
      }

      UserService userService = new UserService();
      Collection<User> users = userService.list();

      StringBuilder builder = new StringBuilder();
      builder.append("<table border='1'>");
      for (User user : users) {
        builder.append("<tr>");
        builder.append("<td>" + user.getUserId() + "</td>");
        builder.append("<td>" + user.getName() + "</td>");
        builder.append("<td>" + user.getEmail() + "</td>");
        builder.append("</tr>");
      }
      builder.append("</table>");
      response.forwardBody(builder.toString());
    } catch (IOException e) {
      log.error(e.getMessage());
    }

  }
}
