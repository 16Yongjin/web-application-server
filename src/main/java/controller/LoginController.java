package controller;

import java.io.IOException;

import service.AuthService;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController extends AbstractController {
  @Override
  public void doPost(HttpRequest request, HttpResponse response) {
    try {

      AuthService authService = new AuthService();

      String userId = request.getParameter("userId");
      String password = request.getParameter("password");

      boolean loginSuccess = authService.login(userId, password);

      if (loginSuccess) {
        response.sendRedirect("/index.html");
        response.addHeader("Set-Cookie", "logined=true");
      } else {
        response.forward("/user/login_failed.html");
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
