package com.accountingapp.datamanager;

import com.accountingapp.data.DBManager;
import com.accountingapp.data.H2DBManager;
import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.data.managers.UserManager;
import com.accountingapp.dto.AccountDTO;
import com.accountingapp.dto.UserDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.enums.TransactionServiceType;
import com.accountingapp.exception.BaseException;
import com.accountingapp.exception.DBException;
import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;
import com.accountingapp.model.User;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static com.accountingapp.data.H2DBManager.getConnection;
import static com.accountingapp.utils.AmountUtil.setDisplayAmount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class AccountsManagerTest {

    private static Logger log = Logger.getLogger(AccountsManagerTest.class);

    private static final int THREADS_COUNT = 100;

    private static DBManager h2Dbmanager;
    private AccountsManager accountsManager;

    @BeforeClass
    public static void setUp() {
        h2Dbmanager = new H2DBManager();
    }

    @Before
    public void setup() {
        h2Dbmanager.populateTestData();
        accountsManager = new AccountsManager();
    }

    @Test
    public void test_getAccountById_No_Account_Present_for_Id() {

        Account account = accountsManager.getAccountById(100L);
        assertThat(account).isNull();
    }

    @Test
    public void test_getAccountById() {

        Account account = accountsManager.getAccountById(1);
        assertThat(account.getUserId()).isEqualTo(1L);
        assertThat(setDisplayAmount(account.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.valueOf(100)));
        assertThat(account.getCurrencyCode()).isEqualTo("USD");
        assertThat(account.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(account.getCreatedOn()).isToday();
    }

    @Test
    public void test_createAccount_Throws_DBException() {

        AccountDTO account = new AccountDTO();
        // user Account already exist
        account.setUserId(1L);
        account.setCurrencyCode("USD");
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        Throwable throwable = catchThrowable(() -> accountsManager.createAccount(account));
        assertThat(throwable).isInstanceOf(DBException.class);
        assertThat(throwable.getCause()).isInstanceOf(SQLException.class);
        assertThat(throwable).hasMessageContaining("Error creating user account for User 1");
    }

    @Test
    public void test_createAccount_Throws_DBException_When_No_User() {

        AccountDTO account = new AccountDTO();
        // user not exists by id
        account.setUserId(10L);
        account.setCurrencyCode("USD");
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        Throwable throwable = catchThrowable(() -> accountsManager.createAccount(account));
        assertThat(throwable).isInstanceOf(DBException.class);
        assertThat(throwable.getCause()).isInstanceOf(SQLException.class);
        assertThat(throwable).hasMessageContaining("Error creating user account for User 1");
    }

    @Test
    public void test_createAccount() {


        UserDTO user = new UserDTO();
        user.setEmailAddress("sometestemail@gmail.com");
        user.setUserName("username");
        user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        User userEntity = new UserManager().createUser(user);

        AccountDTO account = new AccountDTO();
        account.setUserId(userEntity.getUserId());
        account.setCurrencyCode("USD");
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        Account newAccount = accountsManager.createAccount(account);
        assertThat(newAccount).isEqualToIgnoringGivenFields(account, "createdOn", "accountId", "balance", "status");
        assertThat(newAccount.getAccountId()).isEqualTo(8L);
        assertThat(setDisplayAmount(newAccount.getBalance())).isEqualTo(setDisplayAmount(account.getBalance()));
        assertThat(newAccount.getCreatedOn()).isToday();
    }

    @Test
    public void test_deleteAccount_Not_Updated() {

        int count = accountsManager.deleteAccount(100L);
        assertThat(count).isZero();
    }

    @Test
    public void test_deleteAccount() {

        int count = accountsManager.deleteAccount(6L);
        assertThat(count).isOne();

        Account account = accountsManager.getAccountById(6L);
        assertThat(account.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(account.getModifiedOn()).isToday();
    }

    @Test
    public void test_deleteAccount_Fails_account_already_inactive() {

        assertThat(accountsManager.deleteAccount(6L)).isOne();

        Account account = accountsManager.getAccountById(6L);
        assertThat(account.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(account.getModifiedOn()).isToday();

        assertThat(accountsManager.deleteAccount(6L)).isZero();
    }

    @Test
    public void test_getAccountByUserId_No_Account_Present_for_Id() {

        Account account = accountsManager.getAccountByUserId(100L);
        assertThat(account).isNull();
    }

    @Test
    public void test_getAccountByUserId() {

        Account account = accountsManager.getAccountByUserId(1);
        assertThat(account.getUserId()).isEqualTo(1L);
        assertThat(setDisplayAmount(account.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.valueOf(100)));
        assertThat(account.getCurrencyCode()).isEqualTo("USD");
        assertThat(account.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(account.getCreatedOn()).isToday();
    }

    @Test
    public void test_createAccountTransfer_Fail_On_Taking_Account_Lock() throws SQLException {

        final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE AccountId = 5 FOR UPDATE";
        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            // Create a Lock on Sender Account
            Account fromAccount = new QueryRunner().query(connection, SQL_LOCK_ACC, new BeanHandler<>(Account.class));

            if (fromAccount == null) {
                throw new DBException("Unable to Lock Account " + SQL_LOCK_ACC);
            }
            // after lock account 5, try to transfer from account 6 to 5
            // default h2 timeout for acquire lock is 1sec
            BigDecimal transferAmount = BigDecimal.TEN;

            CreateTransactionRequest transaction = new CreateTransactionRequest(transferAmount, 5L, 6L);

            // Initiate Transfer to make it fail on Lock already acquired for sender Account
            Throwable throwable = catchThrowable(() -> accountsManager.createAccountTransfer(transaction));
            assertThat(throwable).isInstanceOf(DBException.class)
                    .hasMessageContaining("Failed to create transfer Timeout trying to lock table");
            connection.commit();
        } catch (SQLException se) {
            try {
                if (Objects.nonNull(connection))
                    connection.rollback();
            }catch (SQLException se2) {
                // Ignore Exs
            }
            // Ignore Ex
        } finally {
            DbUtils.closeQuietly(connection);
        }

        // now Check account 5 and 6 original Balance to verify No Txn
        BigDecimal originalBalance = BigDecimal.valueOf(500);
        assertThat(setDisplayAmount(accountsManager.getAccountById(5).getBalance()))
                .isEqualTo(setDisplayAmount(accountsManager.getAccountById(6).getBalance()))
                .isEqualTo(setDisplayAmount(originalBalance));
    }

    @Test
    public void test_createAccountTransfer_SingleThread() throws BaseException {

        BigDecimal transferAmount = new BigDecimal(50.22);

        CreateTransactionRequest transaction = new CreateTransactionRequest(transferAmount, 3L, 4L);

        Account fromAccount = accountsManager.getAccountById(3L);
        Account toAccount = accountsManager.getAccountById(4L);

        log.debug("From Account Before Transfer " + fromAccount);
        log.debug("To Account Before Transfer " + toAccount);

        BigDecimal fromAccountBalance = fromAccount.getBalance();
        BigDecimal toAccountBalance = toAccount.getBalance();


        long startTime = System.currentTimeMillis();

        Transactions generatedTransaction = accountsManager.createAccountTransfer(transaction);

        long endTime = System.currentTimeMillis();

        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        fromAccount = accountsManager.getAccountById(3L);
        toAccount = accountsManager.getAccountById(4L);

        log.debug("From Account After Transfer " + fromAccount);
        log.debug("To Account After Transfer " + toAccount);

        // Check if Sender Account Debited
        assertThat(setDisplayAmount(fromAccount.getBalance())).isEqualTo(setDisplayAmount(fromAccountBalance.subtract(transferAmount)));

        // Check if Receiver Account Credited
        assertThat(setDisplayAmount(toAccount.getBalance())).isEqualTo(setDisplayAmount(toAccountBalance.add(transferAmount)));


        // Assert the Transaction
        assertThat(generatedTransaction.getCreatedOn()).isToday();
        assertThat(setDisplayAmount(generatedTransaction.getAmount())).isEqualTo(setDisplayAmount(transferAmount));
        assertThat(generatedTransaction.getSenderAccountId()).isEqualTo(transaction.getSenderAccountId());
        assertThat(generatedTransaction.getReceiverAccountId()).isEqualTo(transaction.getReceiverAccountId());
        assertThat(generatedTransaction.getTransactionId()).isNotBlank();
        assertThat(generatedTransaction.getServiceType()).isEqualTo(TransactionServiceType.TRANSFER_FUND);
    }

    @Test
    public void testAccountMultiThreadedTransfer_Throws_LowFundException_After_Balance_Becomes_0() throws Exception {
        // transfer a total of 200USD from 100USD balance in multi-threaded
        // mode, expect half of the transaction fail
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);

        IntStream.rangeClosed(0, THREADS_COUNT)
                .forEach(i -> createNewTransactionThread(latch,
                        new CreateTransactionRequest(BigDecimal.valueOf(2L), 1L, 2L)));

        latch.await();

        Account accountFrom = accountsManager.getAccountById(1);

        Account accountTo = accountsManager.getAccountById(2);

        log.debug("Account From: " + accountFrom);

        log.debug("Account From: " + accountTo);

        // Sender Account Reaches 0 as he transfered 100$ to Receiver
        // Receiver Account Increments by 100$
        assertThat(setDisplayAmount(accountFrom.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.ZERO));
        assertThat(setDisplayAmount(accountTo.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.valueOf(300L)));
    }

    private void createNewTransactionThread(CountDownLatch latch, CreateTransactionRequest transaction) {

        new Thread(() -> {
            try {
                accountsManager.createAccountTransfer(transaction);
            } catch (Exception e) {
                log.error("Error occurred during transfer ", e);
            } finally {
                latch.countDown();
            }
        }).start();
    }

    @Test
    public void testAccountMultiThreadedTransfer_All_Successful() throws Exception {
        // transfer a total of 200USD from 100USD balance in multi-threaded
        // mode, expect half of the transaction fail
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);

        IntStream.rangeClosed(0, THREADS_COUNT)
                .forEach(i -> createNewTransactionThread(latch,
                        new CreateTransactionRequest(BigDecimal.valueOf(2L), 2L, 1L)));

        latch.await();

        Account accountFrom = accountsManager.getAccountById(2L);

        Account accountTo = accountsManager.getAccountById(1L);

        log.debug("Account From: " + accountFrom);

        log.debug("Account From: " + accountTo);

        // Sender Account Reaches 0 as he transfered 100$ to Receiver
        // Receiver Account Increments by 100$
        assertThat(setDisplayAmount(accountFrom.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.ZERO));
        assertThat(setDisplayAmount(accountTo.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.valueOf(300L)));
    }
}
