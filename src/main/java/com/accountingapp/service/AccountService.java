package com.accountingapp.service;

import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;

public interface AccountService {

    Account getAccountById(long accountId);

    Account createAccount(Account account);

    void deleteAccount(long accountId);

    Account getAccountByUserId(long userId);

    Transactions createAccountTransfer(CreateTransactionRequest transactionRequest);
}
