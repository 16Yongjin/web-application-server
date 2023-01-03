package service;

import db.DataBase;
import model.User;

public class AuthService {
  public void signUp(User user) {
    DataBase.addUser(user);
  }
}
