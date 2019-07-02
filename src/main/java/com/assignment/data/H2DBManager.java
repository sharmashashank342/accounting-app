package com.assignment.data;

import com.assignment.data.managers.AccountsManager;
import com.assignment.data.managers.AccountsManagerImpl;
import com.assignment.data.managers.UserManager;
import com.assignment.data.managers.UserManagerImpl;
import com.assignment.exception.DBException;
import org.apache.commons.dbutils.DbUtils;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.assignment.utils.Utils.getProperty;

public class H2DBManager implements DBManager {

	private static final String h2Driver = getProperty("h2_driver");
	private static final String h2ConnectionUrl = getProperty("h2_connection_url");
	private static final String h2User = getProperty("h2_user");
	private static final String h2Password = getProperty("h2_password");

	private final String TEST_DATA_SQL_FILE = "src/test/resources/data.sql";

	private UserManager userDataManager = null;
	private AccountsManagerImpl accountDataManager = null;

	public H2DBManager() {
		DbUtils.loadDriver(h2Driver);
		userDataManager = new UserManagerImpl();
		accountDataManager = new AccountsManagerImpl();
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2ConnectionUrl, h2User, h2Password);

	}

	@Override
	public UserManager getUserManager() {
		return userDataManager;
	}

	@Override
	public AccountsManager getAccountsManager() {
		return accountDataManager;
	}

	@Override
	public void populateTestData() {
		try(Connection conn = getConnection()) {
			RunScript.execute(conn, new FileReader(TEST_DATA_SQL_FILE));
		} catch (SQLException e) {
			throw new DBException("Error populating test data", e);
		} catch (FileNotFoundException e) {
			throw new DBException("Error reading test data file ", e);
		}
	}
}
