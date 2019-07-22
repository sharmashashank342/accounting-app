package com.accountingapp.service;

import com.accountingapp.dto.AccountDTO;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;

public interface AccountService {

    AccountDTO getAccountById(long accountId);

    AccountDTO createAccount(AccountDTO account);

    void deleteAccount(long accountId);

    AccountDTO getAccountByUserId(long userId);

    Transactions createAccountTransfer(CreateTransactionRequest transactionRequest);
}
