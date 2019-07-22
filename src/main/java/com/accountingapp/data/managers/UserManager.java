package com.accountingapp.data.managers;

import com.accountingapp.dto.UserDTO;
import com.accountingapp.model.User;

import java.util.List;

public interface UserManager {
	
	List<User> getAllUsers();

	User getUserById(long userId);

	User createUser(UserDTO user);

	int updateUser(Long userId, UserDTO user);

	int deleteUser(long userId);

}
