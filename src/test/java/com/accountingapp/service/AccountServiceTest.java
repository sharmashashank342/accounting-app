package com.accountingapp.service;

import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.dto.AccountDTO;
import com.accountingapp.enums.Status;
import com.accountingapp.exception.InvalidAccountIdException;
import com.accountingapp.exception.InvalidUserIdException;
import com.accountingapp.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.accountingapp.factory.AccountFactory.getDummyAccount;
import static com.accountingapp.factory.AccountFactory.getDummyAccountDTO;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private AccountsManager accountsManager;

    @Captor
    private ArgumentCaptor<AccountDTO> accountArgumentCaptor;

    @InjectMocks
    private AccountService accountService = new AccountService(accountsManager);

    @Test
    public void test_getAccount_Throws_InvalidAccountIdException_Not_Found() {


        when(accountsManager.getAccountById(anyLong()))
                .thenReturn(null);

        Throwable throwable = catchThrowable(() -> accountService.getAccountById(1L));

        assertThat(throwable).isInstanceOf(InvalidAccountIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 404)
                .hasMessage("Invalid Account Id 1");

        verify(accountsManager).getAccountById(1L);

    }

    @Test
    public void test_getAccount_Throws_InvalidAccountIdException_Account_Inactive() {


        Account account = getDummyAccount();
        account.setStatus(Status.INACTIVE);

        when(accountsManager.getAccountById(anyLong()))
                .thenReturn(account);

        assertThatThrownBy(() -> accountService.getAccountById(1L))
                .isInstanceOf(InvalidAccountIdException.class);

        verify(accountsManager).getAccountById(1L);
    }

    @Test
    public void test_getAccount() {


        Account account = getDummyAccount();
        AccountDTO accountDTO = getDummyAccountDTO();

        when(accountsManager.getAccountById(anyLong()))
                .thenReturn(account);

        assertThat(accountService.getAccountById(1L)).isEqualTo(accountDTO);

        verify(accountsManager).getAccountById(1L);
    }

    @Test
    public void test_createAccount() {

        Account account = getDummyAccount();

        when(accountsManager.createAccount(any(AccountDTO.class)))
                .thenReturn(account);

        assertThat(accountService.createAccount(getDummyAccountDTO())).isEqualToIgnoringGivenFields(account, "balance");
        assertThat(getDummyAccountDTO().getBalance().compareTo(account.getBalance())).isZero();

        verify(accountsManager).createAccount(accountArgumentCaptor.capture());

        assertThat(accountArgumentCaptor.getValue()).isEqualToComparingFieldByField(account);
    }

    @Test
    public void test_deleteAccount_Throws_InvalidAccountIdException() {

        when(accountsManager.deleteAccount(anyLong())).thenReturn(0);

        assertThatThrownBy(() -> accountService.deleteAccount(1L))
                .isInstanceOf(InvalidAccountIdException.class);

        verify(accountsManager).deleteAccount(1L);
    }

    @Test
    public void test_deleteAccount() {

        when(accountsManager.deleteAccount(anyLong())).thenReturn(1);

        accountService.deleteAccount(1L);

        verify(accountsManager).deleteAccount(1L);
    }

    @Test
    public void test_getAccountByUserId_Throws_InvalidUserIdException_Not_Found() {

        when(accountsManager.getAccountByUserId(anyLong()))
                .thenReturn(null);

        Throwable throwable = catchThrowable(() -> accountService.getAccountByUserId(1L));
        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 404)
                .hasMessage("Invalid User Id 1");

        verify(accountsManager).getAccountByUserId(1L);

    }

    @Test
    public void test_getAccountByUserId_Throws_InvalidUserIdException_Account_Inactive() {


        Account account = getDummyAccount();
        account.setStatus(Status.INACTIVE);

        when(accountsManager.getAccountByUserId(anyLong()))
                .thenReturn(account);

        Throwable throwable = catchThrowable(() -> accountService.getAccountByUserId(1L));
        assertThat(throwable).isInstanceOf(InvalidUserIdException.class)
                .hasFieldOrPropertyWithValue("statusCode", 400)
                .hasMessage("Invalid User Id 1");

        verify(accountsManager).getAccountByUserId(1L);
    }

    @Test
    public void test_getAccountByUserId() {


        Account account = getDummyAccount();
        AccountDTO accountDTO = getDummyAccountDTO();

        when(accountsManager.getAccountByUserId(anyLong()))
                .thenReturn(account);

        assertThat(accountService.getAccountByUserId(1L)).isEqualTo(accountDTO);

        verify(accountsManager).getAccountByUserId(1L);
    }
}
