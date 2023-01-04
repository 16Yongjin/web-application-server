package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import service.AuthService;
import service.UserService;
import util.HttpRequest;
import util.LoginChecker;
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

            boolean logined = LoginChecker.isLogined(request);

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
                    response302LoginSuccess(dos);
                } else {
                    responseResource(out, "/user/login_failed.html");
                    responseCookie(dos, "logined", "false");
                    responseBody(dos, "".getBytes());
                }

            } else if (request.method.equals(HttpMethods.GET) && request.path.equals("/user/list")) {
                if (!logined) {
                    responseResource(out, "/user/login.html");
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
                byte[] body = builder.toString().getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if (request.path.endsWith(".css")) {
                byte[] body = Files.readAllBytes(new File("./webapp" + request.path).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            } else if (request.method.equals(HttpMethods.GET)) {
                responseResource(out, request.path);
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

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
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

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response302LoginSuccess(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Location: /index.html\r\n");
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
