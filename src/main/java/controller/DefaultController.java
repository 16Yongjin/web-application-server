package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class DefaultController extends AbstractController {
  @Override
  public void doGet(HttpRequest request, HttpResponse response) {
    response.forward(request.getPath());
  }
}
