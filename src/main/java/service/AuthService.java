package service;

import db.DataBase;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  public boolean signUp(User user) {
    if (DataBase.findUserById(user.getUserId()) != null) {
      return false;
    }

    DataBase.addUser(user);

    return true;
  }

  public boolean login(String userId, String password) {
    User user = DataBase.findUserById(userId);

    if (user == null)
      return false;

    log.info(user.toString());

    if (user.getPassword().equals(password)) {
      return true;
    } else {
      return false;
    }
  }
}
