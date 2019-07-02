package com.assignment.service;

import com.assignment.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(long userId);

    User createUser(User user);

    void updateUser(long userId, User user);

    void deleteUser(long userId);
}
