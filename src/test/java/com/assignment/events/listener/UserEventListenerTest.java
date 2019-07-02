package com.assignment.events.listener;

import com.assignment.controller.RestController;
import com.assignment.data.managers.AccountsManager;
import com.assignment.events.Domain;
import com.assignment.events.EventDTO;
import com.assignment.events.EventType;
import com.assignment.model.Account;
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
        assertThat(RestController.removeInactiveUser(1L)).isFalse();
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
        assertThat(RestController.removeInactiveUser(1L)).isFalse();
    }
}
