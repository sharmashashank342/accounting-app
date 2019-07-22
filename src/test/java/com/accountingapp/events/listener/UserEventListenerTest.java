package com.accountingapp.events.listener;

import com.accountingapp.controller.RestController;
import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.events.Domain;
import com.accountingapp.events.EventDTO;
import com.accountingapp.events.EventType;
import com.accountingapp.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserEventListenerTest {

    @Mock
    private AccountsManager accountsManager;

    @InjectMocks
    private UserEventListener userEventListener = new UserEventListener(accountsManager);

    @Test
    public void test_handleApplicationEvent_Event_Already_Handled() {

        EventDTO<Long> eventDTO = new EventDTO<>();
        eventDTO.setHandled(true);
        eventDTO.setEventType(EventType.USER_DEACTIVATED);
        eventDTO.setDomain(Domain.USER);
        eventDTO.setEventParam(1L);

        Account account = new Account();
        account.setAccountId(1L);
        account.setUserId(1L);


        when(accountsManager.getAccountById(anyLong())).thenReturn(account);
        when(accountsManager.deleteAccount(anyLong())).thenReturn(1);

        userEventListener.handleApplicationEvent(eventDTO);

        verify(accountsManager, never()).getAccountById(1L);
        verify(accountsManager, never()).deleteAccount(1L);
    }

    @Test
    public void test_handleApplicationEvent() {

        EventDTO<Long> eventDTO = new EventDTO<>();
        eventDTO.setHandled(false);
        eventDTO.setEventType(EventType.USER_DEACTIVATED);
        eventDTO.setDomain(Domain.USER);
        eventDTO.setEventParam(1L);

        Account account = new Account();
        account.setAccountId(1L);
        account.setUserId(1L);


        when(accountsManager.getAccountById(anyLong())).thenReturn(account);
        when(accountsManager.deleteAccount(anyLong())).thenReturn(1);

        userEventListener.handleApplicationEvent(eventDTO);

        verify(accountsManager).getAccountById(1L);
        verify(accountsManager).deleteAccount(1L);
    }
}
