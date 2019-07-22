package com.accountingapp.data.managers;

import com.accountingapp.dto.UserDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.exception.DBException;
import com.accountingapp.model.User;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.List;

import static com.accountingapp.data.H2DBManager.getConnection;

// TODO: Somehow Differentiate Buisness Exceptions and Actual DB Exceptions for Status Codes
public class UserManagerImpl implements UserManager {
	
    private static Logger log = Logger.getLogger(UserManagerImpl.class);
    private static final String SQL_GET_USER = "SELECT * FROM User WHERE UserId = ?";
    private static final String SQL_GET_USERS = "SELECT * FROM User";
    private static final String SQL_INSERT_USER = "INSERT INTO User (UserName, EmailAddress, Status, CreatedOn) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE User SET UserName = ?, EmailAddress = ?, ModifiedOn = ? WHERE UserId = ?";
    private static final String SQL_DELETE_USER = "UPDATE User SET Status = ?, ModifiedOn = ? WHERE UserId = ? AND Status='ACTIVE'";
    
    /**
     * Find all users
     */
    @Override
    public List<User> getAllUsers() {
        try(Connection connection = getConnection()) {
            QueryRunner runner = new QueryRunner();
            return runner.query(connection, SQL_GET_USERS, new BeanListHandler<>(User.class));
        } catch (SQLException e) {
            throw new DBException("Error getting Users ", e);
        }
    }
    
    /**
     * Find user by userId
     * Using JDBC Connection and Prepared Statement
     */
    @Override
    public User getUserById(long userId) {

        ResultSet resultSet = null;
        User user = null;
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_USER)) {
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getLong("UserId"), resultSet.getString("UserName"),
                        resultSet.getString("EmailAddress"), Status.valueOf(resultSet.getString("Status")),
                        resultSet.getTimestamp("CreatedOn"), resultSet.getTimestamp("ModifiedOn"));
            }
            return user;
        } catch (SQLException e) {
            throw new DBException("Error getting User ", e);
        } finally {
            DbUtils.closeQuietly(resultSet);
        }
    }
    
    /**
     * Create User
     * @param user
     */
    @Override
    public User createUser(UserDTO user) {
        try(Connection connection = getConnection()) {
            QueryRunner runner = new QueryRunner();
            long userId =  runner.insert(connection, SQL_INSERT_USER, new BeanHandler<>(User.class), user.getUserName(),
                    user.getEmailAddress(), Status.ACTIVE.name(), new Timestamp(System.currentTimeMillis()))
                    .getUserId();
            // As we just get generated Id in create call Resultset
            // so we need to get the whole row back by id
            return getUserById(userId);
        } catch (SQLException e) {
            log.error("Error Inserting User " + user);
            throw new DBException("Error creating user", e);
        }
    }
    
    /**
     * Update User
     */
    public int updateUser(Long userId, UserDTO user) {
        try(Connection connection = getConnection()) {
            return new QueryRunner().update(connection, SQL_UPDATE_USER, user.getUserName(), user.getEmailAddress(),
                    new Timestamp(System.currentTimeMillis()), userId);
        } catch (SQLException e) {
            log.error("Error Updating User " + user);
            throw new DBException("Error update user", e);
        }
    }
    
    /**
     * Inactivate User
     * Never Hard Delete any Data from Database
     */
    public int deleteUser(long userId) {
        try (Connection connection = getConnection()) {
            return new QueryRunner().update(connection, SQL_DELETE_USER, Status.INACTIVE.name(), new Timestamp(System.currentTimeMillis()), userId);
        } catch (SQLException e) {
            log.error("Error Deleting User :" + userId);
            throw new DBException("Error Deleting User ID:"+ userId, e);
        }
    }

}
