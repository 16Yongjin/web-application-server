package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
  private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

  private final DataOutputStream dos;

  public HttpResponse(OutputStream out) {
    this.dos = new DataOutputStream(out);
  }

  public void forward(String url) throws IOException {
    byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

    Pattern pattern = Pattern.compile("\\.(\\w{2,4})$");
    Matcher matcher = pattern.matcher(url);
    matcher.find();
    String fileExtension = matcher.group(1);

    response200Header("text/" + fileExtension, body.length);
    responseBody(body);
  }

  public void forwardBody(String bodyString) throws IOException {
    byte[] body = bodyString.getBytes();
    response200Header("text/html", body.length);
    responseBody(body);
  }

  public void sendRedirect(String location) {
    try {
      dos.writeBytes("HTTP/1.1 302 Found \r\n");
      dos.writeBytes("Location: " + location + "\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public void addHeader(String key, String value) throws IOException {
    dos.writeBytes(key + ":  " + value + "\r\n");
  }

  private void response200Header(String contentType, int lengthOfBodyContent) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void responseBody(byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

}
