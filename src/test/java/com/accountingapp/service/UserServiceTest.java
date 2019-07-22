package com.accountingapp.service;

import com.accountingapp.data.managers.UserManager;
import com.accountingapp.dto.UserDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.exception.InvalidUserIdException;
import com.accountingapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.accountingapp.factory.UserFactory.getDummyUsers;
import static com.accountingapp.factory.UserFactory.getDummyUsersDTO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserManager userManager;

    @Captor
    private ArgumentCaptor<UserDTO> userArgumentCaptor;

    @InjectMocks
    private UserService userService = new UserService(userManager);

    @Test
    public void test_getAllUsers_Returns_EmptyList() {

        when(userManager.getAllUsers()).thenReturn(emptyList());

        assertThat(userService.getAllUsers()).isEmpty();

        verify(userManager).getAllUsers();
    }

    @Test
    public void test_getAllUsers_Returns_1_Inactive_User() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        User user = getDummyUsers(timestamp, timestamp).get(0);
        user.setStatus(Status.INACTIVE);

        when(userManager.getAllUsers()).thenReturn(Collections.singletonList(user));

        assertThat(userService.getAllUsers()).isEmpty();

        verify(userManager).getAllUsers();
    }

    @Test
    public void test_getAllUsers() {

        Timestamp createdOn = Timestamp.valueOf(LocalDateTime.now().minusWeeks(1));
        Timestamp modifiedOn = new Timestamp(System.currentTimeMillis());

        List<User> users = getDummyUsers(createdOn, modifiedOn);
        List<UserDTO> userDTOS = users.stream()
                .filter(user -> user.getStatus() == Status.ACTIVE)
                .map(user -> new ObjectMapper().convertValue(user, UserDTO.class))
                .collect(toList());

        when(userManager.getAllUsers()).thenReturn(getDummyUsers(createdOn, modifiedOn));

        assertThat(userService.getAllUsers()).hasSize(4).isEqualTo(userDTOS);

        verify(userManager).getAllUsers();
    }

    @Test
    public void test_getUserById_Throws_InvalidUserIdException_when_not_found() {

        when(userManager.getUserById(anyLong())).thenReturn(null);

        Throwable throwable = catchThrowable(() -> userService.getUserById(1L));

        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 404)
                .hasMessage("Invalid User Id 1");

        verify(userManager).getUserById(1L);
    }

    @Test
    public void test_getUserById_Throws_InvalidUserIdException_when_inactive() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);
        user.setStatus(Status.INACTIVE);

        when(userManager.getUserById(anyLong())).thenReturn(user);

        Throwable throwable = catchThrowable(() -> userService.getUserById(1L));

        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 400)
                .hasMessage("Invalid User Id 1");

        verify(userManager).getUserById(1L);
    }

    @Test
    public void test_getUserById() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);

        when(userManager.getUserById(anyLong())).thenReturn(user);

        assertThat(userService.getUserById(1L)).isEqualTo(user);

        verify(userManager).getUserById(1L);
    }

    @Test
    public void test_createUser() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);
        UserDTO userDTO = getDummyUsersDTO(timestamp, timestamp).get(0);

        when(userManager.createUser(any(UserDTO.class))).thenReturn(user);

        assertThat(userService.createUser(userDTO)).isEqualToComparingFieldByField(user);

        verify(userManager).createUser(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualToIgnoringGivenFields(user, "status");
    }

    @Test
    public void test_updateUser_Throws_InvalidUserIdException() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);
        UserDTO userDTO = getDummyUsersDTO(timestamp, timestamp).get(0);

        when(userManager.updateUser(anyLong(), any(UserDTO.class))).thenReturn(0);

        Throwable throwable = catchThrowable(() -> userService.updateUser(2L, userDTO));

        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 404)
                .hasMessage("Invalid User Id 2");

        verify(userManager).updateUser(eq(2L), userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualToIgnoringGivenFields(user, "status");
    }

    @Test
    public void test_updateUser() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);
        UserDTO userDTO = getDummyUsersDTO(timestamp, timestamp).get(0);

        when(userManager.updateUser(anyLong(), any(UserDTO.class))).thenReturn(1);

        userService.updateUser(2, userDTO);

        verify(userManager).updateUser(eq(2L), userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualToIgnoringGivenFields(user, "status");
    }

    @Test
    public void test_deleteUser_Throws_InvalidUserIdException() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);
        user.setStatus(Status.INACTIVE);

        when(userManager.deleteUser(anyLong())).thenReturn(0);

        Throwable throwable = catchThrowable(() -> userService.deleteUser(1L));

        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 404)
                .hasMessage("Invalid User Id 1");

        verify(userManager).deleteUser(1L);
    }

    @Test
    public void test_deleteUser() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        when(userManager.deleteUser(anyLong())).thenReturn(1);

        userService.deleteUser(1L);

        verify(userManager).deleteUser(1L);
    }
}
