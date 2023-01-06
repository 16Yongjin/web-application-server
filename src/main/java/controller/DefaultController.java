package controller;

import java.io.IOException;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class DefaultController extends AbstractController {
  @Override
  public void doGet(HttpRequest request, HttpResponse response) {
    try {
      response.forward(request.getPath());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
