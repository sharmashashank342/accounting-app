package com.assignment.factory;

import com.assignment.enums.Status;
import com.assignment.model.Account;

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
}
