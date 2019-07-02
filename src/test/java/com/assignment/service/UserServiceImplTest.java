package com.assignment.service;

import com.assignment.data.managers.UserManager;
import com.assignment.enums.Status;
import com.assignment.exception.InvalidUserIdException;
import com.assignment.model.User;
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

import static com.assignment.factory.UserFactory.getDummyActiveUsers;
import static com.assignment.factory.UserFactory.getDummyUsers;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserManager userManager;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @InjectMocks
    private UserService userService = new UserServiceImpl(userManager);

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

        when(userManager.getAllUsers()).thenReturn(getDummyUsers(createdOn, modifiedOn));

        assertThat(userService.getAllUsers()).hasSize(4).isEqualTo(getDummyActiveUsers(createdOn, modifiedOn));

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

        when(userManager.createUser(any(User.class))).thenReturn(user);

        assertThat(userService.createUser(user)).isEqualTo(user);

        verify(userManager).createUser(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void test_updateUser_Throws_InvalidUserIdException() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);

        when(userManager.updateUser(anyLong(), any(User.class))).thenReturn(0);

        Throwable throwable = catchThrowable(() -> userService.updateUser(2L, user));

        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 404)
                .hasMessage("Invalid User Id 2");

        verify(userManager).updateUser(eq(2L), userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void test_updateUser() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user = getDummyUsers(timestamp, timestamp).get(0);

        when(userManager.updateUser(anyLong(), any(User.class))).thenReturn(1);

        userService.updateUser(2, user);

        verify(userManager).updateUser(eq(2l), userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
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
