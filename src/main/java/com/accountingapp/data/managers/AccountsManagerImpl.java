package com.accountingapp.data.managers;

import com.accountingapp.dto.AccountDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.enums.TransactionEntryType;
import com.accountingapp.enums.TransactionServiceType;
import com.accountingapp.exception.DBException;
import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.TransactionDetails;
import com.accountingapp.model.Transactions;
import com.accountingapp.utils.Utils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static com.accountingapp.data.H2DBManager.getConnection;

// TODO: Somehow Differentiate Buisness Exceptions and Actual DB Exceptions for Status Codes
public class AccountsManagerImpl implements AccountsManager {

	private static Logger log = Logger.getLogger(AccountsManagerImpl.class);
	private static final String SQL_GET_ACC = "SELECT * FROM Account WHERE AccountId = ?";
	private static final String SQL_GET_USER_ACC = "SELECT * FROM Account WHERE UserId = ?";
	private static final String SQL_GET_ACC_FOR_UPDATE = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
	private static final String SQL_CREATE_ACC = "INSERT INTO Account (UserId, Balance, CurrencyCode, Status, CreatedOn) VALUES (?, ?, ?, ?, ?)";
	private static final String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ?, ModifiedOn = ? WHERE AccountId = ?";
	private static final String SQL_DELETE_ACC = "UPDATE Account SET Status = ?, ModifiedOn = ? WHERE AccountId = ? AND Status='ACTIVE'";

	private static final String SQL_CREATE_TXN = "INSERT INTO Transactions (TransactionId, SenderAccountId, ReceiverAccountId, CreatedOn, TransactionDate, Amount, ServiceType) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_CREATE_TXN_DETAIL = "INSERT INTO TransactionDetails (TransactionDetailsId, TransactionId, AccountId, CreatedOn, SequenceNo, EntryType) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SQL_GET_TXN = "SELECT * FROM Transactions WHERE TransactionId = ?";

	private static final String ACCOUNT_CREATE_ERROR = "Error creating user account for User ";
	private static final String MESSAGE = " Message : ";
	
	/**
	 * Get account by id
	 */
	@Override
	public Account getAccountById(long accountId) {
		return getAccount(SQL_GET_ACC, accountId);
	}

	/**
	 * Create account
	 */
	@Override
	public Account createAccount(AccountDTO account) {
		try(Connection connection = getConnection()) {
			QueryRunner runner = new QueryRunner();
			Account newAccount = runner.insert(connection, SQL_CREATE_ACC, new BeanHandler<>(Account.class), account.getUserId(),
					BigDecimal.ZERO, account.getCurrencyCode(), Status.ACTIVE.name(),
					new Timestamp(System.currentTimeMillis()));
			// As we just get generated Id in create call Resultset
			// so we need to get the whole row back by id
			return getAccountById(newAccount.getAccountId());
		} catch (SQLException e) {
			throw new DBException(ACCOUNT_CREATE_ERROR + account.getUserId()+MESSAGE+e.getMessage(), e);
		}
	}
	
	/**
	 * Inactivate Account
	 * Never Hard Delete any Data from Database
	 */
	@Override
	public int deleteAccount(long accountId) {
		try(Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_ACC)) {
			stmt.setString(1, Status.INACTIVE.name());
			stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			stmt.setLong(3, accountId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DBException("Error deleting user account " + accountId, e);
		}
	}

	@Override
	public Account getAccountByUserId(long userId) {
		return getAccount(SQL_GET_USER_ACC, userId);
	}

	private Account getAccount(String sql, long param) {
		try (Connection connection = getConnection()) {
			QueryRunner runner = new QueryRunner();
			return runner.query(connection, sql, new BeanHandler<>(Account.class), param);
		} catch (SQLException e) {
			throw new DBException("Error reading account data"+e.getMessage(), e);
		}
	}

	/**
	 * Create Transaction between 2 Accounts
	 */
	@Override
	public Transactions createAccountTransfer(CreateTransactionRequest createTransactionRequest) {
		PreparedStatement updateStmt = null;
		Connection connection = null;
		String transactionId = null;

		try {
			connection = getConnection();
			connection.setAutoCommit(false);

			Account fromAccount = new QueryRunner().query(connection, SQL_GET_ACC_FOR_UPDATE, new BeanHandler<>(Account.class),
					createTransactionRequest.getSenderAccountId());


			Account toAccount = new QueryRunner().query(connection, SQL_GET_ACC_FOR_UPDATE, new BeanHandler<>(Account.class),
					createTransactionRequest.getReceiverAccountId());

			// check locking status
			if (fromAccount == null) {
				throw new DBException("Fail to lock sender account");
			}

			if (toAccount == null) {
				throw new DBException("Fail to lock receiver account");
			}

			// check enough fund in source account
			BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(createTransactionRequest.getAmount());
			if (fromAccountLeftOver.compareTo(BigDecimal.ZERO) < 0) {
				throw new DBException("Not enough Fund in source Account", 400);
			}
			// proceed with update
			updateStmt = connection.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, fromAccountLeftOver);
			updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			updateStmt.setLong(3, createTransactionRequest.getSenderAccountId());

			updateStmt.addBatch();
			updateStmt.setBigDecimal(1, toAccount.getBalance().add(createTransactionRequest.getAmount()));
			updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			updateStmt.setLong(3, createTransactionRequest.getReceiverAccountId());
			updateStmt.addBatch();

			updateStmt.executeBatch();

			transactionId = createTransaction(connection, createTransactionRequest);


			// If there is no error, commit the transaction
			connection.commit();

		} catch (Exception ex) {
			// rollback transaction if exception occurs
			log.error("Transaction Failed, rollback initiated for " + createTransactionRequest,
					ex);
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException se2) {
				// Ignored
			}
			if (ex instanceof DBException)
				throw (DBException) ex;
			throw new DBException("Failed to create transfer "+ex.getMessage());
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(updateStmt);
		}

		return getTransactionById(transactionId);
	}

	private String createTransaction(Connection connection, CreateTransactionRequest createTransactionRequest) {
		try {
			String transactionId = Utils.getNewTransactionId();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			log.info("Txn initiated "+transactionId);

			// New Txn
			// TODO: take Txn serviceType from Request Param
			new QueryRunner().insert(connection, SQL_CREATE_TXN, new BeanHandler<>(Transactions.class), transactionId,
					createTransactionRequest.getSenderAccountId(), createTransactionRequest.getReceiverAccountId(),
					timestamp, timestamp, createTransactionRequest.getAmount(), TransactionServiceType.TRANSFER_FUND.name());


			// Create Dual Pairs for Txn
			// Sender
			new QueryRunner().insert(connection, SQL_CREATE_TXN_DETAIL, new BeanHandler<>(TransactionDetails.class),
					UUID.randomUUID().toString(), transactionId, createTransactionRequest.getSenderAccountId(),
					timestamp, 1, TransactionEntryType.DR.name());

			// Receiver
			new QueryRunner().insert(connection, SQL_CREATE_TXN_DETAIL, new BeanHandler<>(TransactionDetails.class),
					UUID.randomUUID().toString(), transactionId, createTransactionRequest.getReceiverAccountId(),
					timestamp, 2, TransactionEntryType.CR.name());

			// Return Created Txn Id
			return transactionId;
		}catch (SQLException e) {
			throw new DBException("Error Capturing Transaction", e);
		}
	}

	private Transactions getTransactionById(String transactionId) {
		try (Connection connection = getConnection()) {
			return new QueryRunner().query(connection, SQL_GET_TXN, new BeanHandler<>(Transactions.class), transactionId);
		} catch (SQLException e) {
			throw new DBException("Error Getting Transaction "+transactionId, e);
		}
	}
}
