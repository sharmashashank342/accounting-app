package com.accountingapp.service;

import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.dto.AccountDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.exception.InvalidAccountIdException;
import com.accountingapp.exception.InvalidUserIdException;
import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Predicate;

public class AccountService {

    private Predicate<Account> activeAccounts = account -> account.getStatus() == Status.ACTIVE;

    private AccountsManager accountsManager;

    public AccountService(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    
    public AccountDTO getAccountById(long accountId) {
        Account account = Optional.ofNullable(accountsManager.getAccountById(accountId))
                .orElseThrow(() -> new InvalidAccountIdException(accountId, Response.Status.NOT_FOUND.getStatusCode()));

        if (!activeAccounts.test(account)) {
            throw new InvalidAccountIdException(accountId);
        }

        return convertToAccountDTO(account);
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {

        Account account = accountsManager.createAccount(accountDTO);
        return convertToAccountDTO(account);
    }

    private AccountDTO convertToAccountDTO(Account account) {
        return new ObjectMapper().convertValue(account, AccountDTO.class);
    }

    
    public void deleteAccount(long accountId) {
        int deleteCount = accountsManager.deleteAccount(accountId);

        // Delete Count Can't Exceed 1 as account Id is primary key
        if (deleteCount != 1) {
            throw new InvalidAccountIdException(accountId, Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    public AccountDTO getAccountByUserId(long userId) {
        Account account = Optional.ofNullable(accountsManager.getAccountByUserId(userId))
                .orElseThrow(() -> new InvalidUserIdException(userId, Response.Status.NOT_FOUND.getStatusCode()));

        if (!activeAccounts.test(account)) {
            throw new InvalidUserIdException(userId);
        }
        return convertToAccountDTO(account);
    }

    public Transactions createAccountTransfer(CreateTransactionRequest transactionRequest) {
        return accountsManager.createAccountTransfer(transactionRequest);
    }

}
