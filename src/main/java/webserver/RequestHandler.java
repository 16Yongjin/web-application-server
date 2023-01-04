package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import service.AuthService;
import util.HttpRequest;
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

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.parseStream(in);

            request.log();

            DataOutputStream dos = new DataOutputStream(out);

            if (request.method.equals(HttpMethods.POST) && request.path.equals("/user/create")) {
                AuthService authService = new AuthService();

                Map<String, String> signUpForm = request.getForm();

                User user = new User(
                        signUpForm.get("userId"),
                        signUpForm.get("password"),
                        signUpForm.get("name"),
                        signUpForm.get("email"));

                log.info(user.toString());

                authService.signUp(user);
                response302(dos, "/index.html");
                responseBody(dos, "".getBytes());
            } else if (request.method.equals(HttpMethods.POST) && request.path.equals("/user/login")) {
                AuthService authService = new AuthService();

                Map<String, String> loginForm = request.getForm();

                String userId = loginForm.get("userId");
                String password = loginForm.get("password");

                boolean loginSuccess = authService.login(userId, password);

                if (loginSuccess) {
                    response302(dos, "/index.html");
                    responseCookie(dos, "logined", "true");
                    responseBody(dos, "".getBytes());
                } else {
                    response302(dos, "/user/login_failed.html");
                    responseCookie(dos, "logined", "false");
                    responseBody(dos, "".getBytes());
                }

            } else if (request.method.equals(HttpMethods.GET)) {
                byte[] htmlBytes = Files.readAllBytes(Paths.get("./webapp" + request.path));
                response200Header(dos, htmlBytes.length);
                responseBody(dos, htmlBytes);
            } else {
                byte[] body = "Hello World".getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseCookie(DataOutputStream dos, String key, String value) {
        try {
            dos.writeBytes("Set-Cookie: " + key + "=" + value + "\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
