package controller;

import java.util.Map;

import model.User;
import service.AuthService;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserController extends AbstractController {
  @Override
  public void doPost(HttpRequest request, HttpResponse response) {
    AuthService authService = new AuthService();

    Map<String, String> signUpForm = request.getForm();

    User user = new User(
        signUpForm.get("userId"),
        signUpForm.get("password"),
        signUpForm.get("name"),
        signUpForm.get("email"));

    log.info(user.toString());

    authService.signUp(user);
    response.sendRedirect("/index.html");
  }
}
