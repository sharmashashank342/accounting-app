package com.accountingapp.data;

import com.accountingapp.exception.DBException;
import org.apache.commons.dbutils.DbUtils;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.accountingapp.utils.Utils.getProperty;

public class H2DBManager implements DBManager {

	private static final String h2Driver = getProperty("h2_driver");
	private static final String h2ConnectionUrl = getProperty("h2_connection_url");
	private static final String h2User = getProperty("h2_user");
	private static final String h2Password = getProperty("h2_password");

	private final String TEST_DATA_SQL_FILE = "src/test/resources/data.sql";

	public H2DBManager() {
		DbUtils.loadDriver(h2Driver);
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2ConnectionUrl, h2User, h2Password);

	}

	@Override
	public void populateData() {
		try(Connection conn = getConnection()) {
			RunScript.execute(conn, new FileReader(TEST_DATA_SQL_FILE));
		} catch (SQLException e) {
			throw new DBException("Error populating test data", e);
		} catch (FileNotFoundException e) {
			throw new DBException("Error reading test data file ", e);
		}
	}
}
