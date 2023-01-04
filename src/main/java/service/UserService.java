package service;

import java.util.Collection;

import db.DataBase;
import model.User;

public class UserService {
  public Collection<User> list() {
    return DataBase.findAll();
  }
}
