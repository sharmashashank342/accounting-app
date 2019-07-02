package com.assignment.data.managers;

import com.assignment.model.User;

import java.util.List;

public interface UserManager {
	
	List<User> getAllUsers();

	User getUserById(long userId);

	User createUser(User user);

	int updateUser(Long userId, User user);

	int deleteUser(long userId);

}
