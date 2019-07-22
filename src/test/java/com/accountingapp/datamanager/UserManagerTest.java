package com.accountingapp.datamanager;

import com.accountingapp.data.DBManager;
import com.accountingapp.data.H2DBManager;
import com.accountingapp.data.managers.UserManager;
import com.accountingapp.dto.UserDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static com.accountingapp.factory.UserFactory.getAllPopulatedUsers;
import static org.assertj.core.api.Assertions.assertThat;

public class UserManagerTest {

    private static DBManager h2Dbmanager;
    private UserManager userManager;

    @BeforeClass
    public static void setUp() {
        h2Dbmanager = new H2DBManager();
    }

    @Before
    public void setup() {
        h2Dbmanager.populateTestData();
        userManager = new UserManager();
    }

    @Test
    public void test_getAllUsers() {

        List<User> users = userManager.getAllUsers();
        assertThat(users).hasSize(8);

        assertThat(users).extracting("userId").containsExactly(getAllPopulatedUsers().stream().map(User::getUserId).toArray());
        assertThat(users).extracting("userName").containsExactly(getAllPopulatedUsers().stream().map(User::getUserName).toArray());
        assertThat(users).extracting("emailAddress").containsExactly(getAllPopulatedUsers().stream().map(User::getEmailAddress).toArray());

        Optional<Timestamp> timestamp = users.stream().map(User::getCreatedOn).sorted().findFirst();
        timestamp.ifPresent(timestamp1 ->  assertThat(timestamp1).isToday());

        // Only 1 User Modified
        assertThat(users.get(7).getModifiedOn()).isNotNull().isToday();

        users.remove(7);

        assertThat(users).extracting("modifiedOn").containsOnlyNulls();
    }

    @Test
    public void test_getUserById_ReturnsNull() {

        // User Not found by id
        assertThat(userManager.getUserById(100L)).isNull();
    }

    @Test
    public void test_getUserById() {

        User user = userManager.getUserById(1L);
        assertThat(user.getUserName()).isEqualTo("shashank");
        assertThat(user.getEmailAddress()).isEqualTo("shashank@gmail.com");
        assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(user.getCreatedOn()).isToday();
    }

    @Test
    public void test_createUser() {

        UserDTO user = new UserDTO();
        user.setUserName("userName");
        user.setEmailAddress("useremail@gmail.com");

        User newUser = userManager.createUser(user);
        assertThat(newUser).isEqualToIgnoringGivenFields(user, "userId", "status", "createdOn");
        assertThat(newUser.getCreatedOn()).isToday();
    }

    @Test
    public void test_updateUser_when_No_User_By_Id() {

        assertThat(userManager.updateUser(100L, new UserDTO())).isZero();
    }

    @Test
    public void test_updateUser() {

        UserDTO user = new UserDTO();
        user.setUserName("newUserName");
        user.setEmailAddress("newEmail@gmail.com");

        assertThat(userManager.updateUser(6L, user)).isOne();
    }

    @Test
    public void test_deleteUser_when_No_User_By_Id() {

        assertThat(userManager.deleteUser(100L)).isZero();
    }

    @Test
    public void test_deleteUser() {

        assertThat(userManager.deleteUser(6L)).isOne();

        User user = userManager.getUserById(6L);
        assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(user.getModifiedOn()).isToday();
    }

    @Test
    public void test_deleteUser_Fails_user_already_inactive() {

        assertThat(userManager.deleteUser(6L)).isOne();

        User user = userManager.getUserById(6L);
        assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(user.getModifiedOn()).isToday();

        assertThat(userManager.deleteUser(6L)).isZero();
    }
}
