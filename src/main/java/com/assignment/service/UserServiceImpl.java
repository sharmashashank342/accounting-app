package com.assignment.service;

import com.assignment.data.managers.UserManager;
import com.assignment.enums.Status;
import com.assignment.exception.InvalidUserIdException;
import com.assignment.model.User;

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
    public List<User> getAllUsers() {
        return userManager
                .getAllUsers()
                .stream()
                .filter(activeUsers)
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
    public User createUser(User user) {
        return userManager.createUser(user);
    }
    
    @Override
    public void updateUser(long userId, User user) {
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
