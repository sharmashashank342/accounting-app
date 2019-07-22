package com.accountingapp.data.managers;

import com.accountingapp.dto.AccountDTO;
import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;


public interface AccountsManager {

    Account getAccountById(long accountId);
    Account createAccount(AccountDTO account);
    int deleteAccount(long accountId);
    Account getAccountByUserId(long userId);

    Transactions createAccountTransfer(CreateTransactionRequest createTransactionRequest);
}
