package controller;

import model.User;
import service.AuthService;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserController extends AbstractController {
  @Override
  public void doPost(HttpRequest request, HttpResponse response) {
    AuthService authService = new AuthService();

    User user = new User(
        request.getParameter("userId"),
        request.getParameter("password"),
        request.getParameter("name"),
        request.getParameter("email"));

    log.info(user.toString());

    authService.signUp(user);
    response.sendRedirect("/index.html");
  }
}
