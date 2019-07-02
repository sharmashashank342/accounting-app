package com.assignment.service;

import com.assignment.model.Account;
import com.assignment.model.CreateTransactionRequest;
import com.assignment.model.Transactions;

public interface AccountService {

    Account getAccountById(long accountId);

    Account createAccount(Account account);

    void deleteAccount(long accountId);

    Account getAccountByUserId(long userId);

    Transactions createAccountTransfer(CreateTransactionRequest transactionRequest);
}
