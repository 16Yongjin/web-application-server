package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import controller.RequestMapping;
import util.HttpMethod;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.parseStream(in);
            HttpResponse response = new HttpResponse(out);

            request.log();

            Controller controller = RequestMapping.getController(request.getPath());
            controller.service(request, response);

            if (request.getMethod().equals(HttpMethod.GET)) {
            } else {
                response.forwardBody("Hello World");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
