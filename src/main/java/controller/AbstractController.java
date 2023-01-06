package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class AbstractController implements Controller {
  protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

  @Override
  public void service(HttpRequest request, HttpResponse response) {
    doPost(request, response);
    doGet(request, response);
  }

  public void doPost(HttpRequest request, HttpResponse response) {
  }

  public void doGet(HttpRequest request, HttpResponse response) {
  }
}
