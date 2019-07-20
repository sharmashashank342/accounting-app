package com.accountingapp.service;

import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.enums.Status;
import com.accountingapp.exception.InvalidAccountIdException;
import com.accountingapp.exception.InvalidRequestException;
import com.accountingapp.exception.InvalidUserIdException;
import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Predicate;

public class AccountServiceImpl implements AccountService {

    private Predicate<Account> activeAccounts = account -> account.getStatus() == Status.ACTIVE;

    private AccountsManager accountsManager;

    public AccountServiceImpl(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    @Override
    public Account getAccountById(long accountId) {
        Account account = Optional.ofNullable(accountsManager.getAccountById(accountId))
                .orElseThrow(() -> new InvalidAccountIdException(accountId, Response.Status.NOT_FOUND.getStatusCode()));

        if (!activeAccounts.test(account)) {
            throw new InvalidAccountIdException(accountId);
        }
        return account;
    }

    @Override
    public Account createAccount(Account account) {
        return accountsManager.createAccount(account);
    }


    @Override
    public void deleteAccount(long accountId) throws InvalidRequestException {
        int deleteCount = accountsManager.deleteAccount(accountId);

        // Delete Count Can't Exceed 1 as account Id is primary key
        if (deleteCount != 1) {
            throw new InvalidAccountIdException(accountId, Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Override
    public Account getAccountByUserId(long userId) {
        Account account = Optional.ofNullable(accountsManager.getAccountByUserId(userId))
                .orElseThrow(() -> new InvalidUserIdException(userId, Response.Status.NOT_FOUND.getStatusCode()));

        if (!activeAccounts.test(account)) {
            throw new InvalidUserIdException(userId);
        }
        return account;
    }

    @Override
    public Transactions createAccountTransfer(CreateTransactionRequest transactionRequest) {
        return accountsManager.createAccountTransfer(transactionRequest);
    }

}
