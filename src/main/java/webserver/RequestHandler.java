package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import controller.Controller;
import controller.CreateUserController;
import controller.DefaultController;
import controller.ListUserController;
import controller.LoginController;
import util.HttpMethods;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        Map<String, Controller> controllers = Maps.newHashMap();
        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/list", new ListUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("DEFAULT", new DefaultController());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.parseStream(in);
            HttpResponse response = new HttpResponse(out);

            request.log();

            Controller controller;
            if (controllers.containsKey(request.getPath())) {
                controller = controllers.get(request.getPath());
            } else {
                controller = controllers.get("DEFAULT");
            }

            controller.service(request, response);

            if (request.getMethod().equals(HttpMethods.GET)) {
            } else {
                response.forwardBody("Hello World");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
