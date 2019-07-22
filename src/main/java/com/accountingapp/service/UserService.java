package com.accountingapp.service;

import com.accountingapp.dto.UserDTO;
import com.accountingapp.model.User;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    User getUserById(long userId);

    UserDTO createUser(UserDTO user);

    void updateUser(long userId, UserDTO user);

    void deleteUser(long userId);
}
