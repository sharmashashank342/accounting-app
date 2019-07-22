package com.accountingapp.service;

import com.accountingapp.data.managers.UserManager;
import com.accountingapp.dto.UserDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.exception.InvalidUserIdException;
import com.accountingapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class UserServiceImpl implements UserService {

    private Predicate<User> activeUsers = user -> user.getStatus() == Status.ACTIVE;
 
	private UserManager userManager;

    public UserServiceImpl(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userManager
                .getAllUsers()
                .stream()
                .filter(activeUsers)
                .map(this::convertToUserDTO)
                .collect(toList());
    }

    @Override
    public User getUserById(long userId) {

        User user = Optional.ofNullable(userManager.getUserById(userId))
                .orElseThrow(() -> new InvalidUserIdException(userId, Response.Status.NOT_FOUND.getStatusCode()));

        if (!activeUsers.test(user)) {
            throw new InvalidUserIdException(userId);
        }
        return user;
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        return Optional.of(userManager.createUser(user))
                .map(this::convertToUserDTO)
                .get();
    }

    private UserDTO convertToUserDTO(User user) {
        return new ObjectMapper().convertValue(user, UserDTO.class);
    }

    @Override
    public void updateUser(long userId, UserDTO user) {
        int updateCount = userManager.updateUser(userId, user);
        if (updateCount != 1) {
            throw new InvalidUserIdException(userId, Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Override
    public void deleteUser(long userId) {
        int deleteCount = userManager.deleteUser(userId);

        // Delete Count Can't Exceed 1 as user Id is primary key
        if (deleteCount != 1) {
            throw new InvalidUserIdException(userId, Response.Status.NOT_FOUND.getStatusCode());
        }
    }
}
