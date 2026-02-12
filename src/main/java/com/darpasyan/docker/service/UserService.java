package com.darpasyan.docker.service;

import com.darpasyan.docker.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();
    User createUser(User user);
    User updateUser(int id, User user);
    void deleteUser(int id);
}
