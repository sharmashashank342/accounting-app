package com.accountingapp.factory;

import com.accountingapp.dto.UserDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class UserFactory {

    public static List<User> getDummyUsers(Timestamp createdOn, Timestamp modifiedOn) {

        List<User> users = new ArrayList<>(6);

        users.add(new User(1L, "User 1", "user1@gmail.com", Status.ACTIVE, createdOn, null));
        users.add(new User(2L, "User 2", "user2@gmail.com", Status.ACTIVE, createdOn, null));
        users.add(new User(3L, "User 3", "user3@gmail.com", Status.INACTIVE, createdOn, modifiedOn));
        users.add(new User(4L, "User 4", "user4@gmail.com", Status.ACTIVE, createdOn, null));
        users.add(new User(5L, "User 5", "user5@gmail.com", Status.INACTIVE, createdOn, modifiedOn));
        users.add(new User(6L, "User 6", "user6@gmail.com", Status.ACTIVE, createdOn, null));

        return users;
    }

    public static List<User> getDummyActiveUsers(Timestamp createdOn, Timestamp modifiedOn) {

        return getDummyUsers(createdOn, modifiedOn).stream()
                .filter(user -> user.getStatus() == Status.ACTIVE)
                .collect(toList());
    }
    
    public static List<User> getAllPopulatedUsers() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        List<User> users = new ArrayList<>(6);

        users.add(new User(1L, "shashank", "shashank@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(2L, "arun", "arun@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(3L, "ravi", "ravi@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(4L, "ali", "ali@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(5L, "sachin", "sachin@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(6L, "virat", "virat@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(7L, "non_account_user", "non_account_user@gmail.com", Status.ACTIVE, timestamp, null));
        users.add(new User(8L, "non_active", "non_active@gmail.com", Status.INACTIVE, timestamp, timestamp));

        return users;
    }

    public static List<UserDTO> getAllPopulatedUsersDTO() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        List<UserDTO> users = new ArrayList<>(6);

        users.add(new UserDTO(1L, "shashank", "shashank@gmail.com", timestamp, null));
        users.add(new UserDTO(2L, "arun", "arun@gmail.com", timestamp, null));
        users.add(new UserDTO(3L, "ravi", "ravi@gmail.com", timestamp, null));
        users.add(new UserDTO(4L, "ali", "ali@gmail.com", timestamp, null));
        users.add(new UserDTO(5L, "sachin", "sachin@gmail.com", timestamp, null));
        users.add(new UserDTO(6L, "virat", "virat@gmail.com", timestamp, null));
        users.add(new UserDTO(7L, "non_account_user", "non_account_user@gmail.com", timestamp, null));

        return users;
    }

    public static List<UserDTO> getDummyUsersDTO(Timestamp createdOn, Timestamp modifiedOn) {

        ObjectMapper objectMapper = new ObjectMapper();

        return getDummyUsers(createdOn, modifiedOn)
                .stream()
                .map(user -> objectMapper.convertValue(user, UserDTO.class))
                .collect(toList());
    }
}
