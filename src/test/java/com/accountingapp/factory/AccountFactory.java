package com.accountingapp.factory;

import com.accountingapp.dto.AccountDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.model.Account;

import java.math.BigDecimal;

public class AccountFactory {

    public static Account getDummyAccount() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setUserId(1L);
        account.setBalance(BigDecimal.TEN);
        account.setStatus(Status.ACTIVE);
        account.setCurrencyCode("INR");
        return account;
    }

    public static AccountDTO getDummyAccountDTO() {
        AccountDTO account = new AccountDTO();
        account.setAccountId(1L);
        account.setUserId(1L);
        account.setBalance(BigDecimal.TEN);
        account.setCurrencyCode("INR");
        return account;
    }
}
