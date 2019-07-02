package com.assignment.data.managers;

import com.assignment.model.Account;
import com.assignment.model.CreateTransactionRequest;
import com.assignment.model.Transactions;


public interface AccountsManager {

    Account getAccountById(long accountId);
    Account createAccount(Account account);
    int deleteAccount(long accountId);
    Account getAccountByUserId(long userId);

    Transactions createAccountTransfer(CreateTransactionRequest createTransactionRequest);
}
