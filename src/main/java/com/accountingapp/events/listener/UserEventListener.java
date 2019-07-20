package com.accountingapp.events.listener;

import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.events.Domain;
import com.accountingapp.events.EventDTO;
import com.accountingapp.events.EventType;
import com.accountingapp.model.Account;

public class UserEventListener implements EventListener{

    private AccountsManager accountsManager;

    public UserEventListener(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    @Override
    public void handleApplicationEvent(EventDTO<?> eventDTO) {

        // if Event is already Handled
        if (eventDTO.isHandled())
            return;

        if (eventDTO.getDomain() == Domain.USER) {

            if (eventDTO.getEventType() == EventType.USER_DEACTIVATED) {

                deactivateUserAccount(Long.valueOf(eventDTO.getEventParam().toString()));
                // Set for Further Use if any
                eventDTO.setHandled(true);
            }
        }
    }

    private void deactivateUserAccount(Long userId) {

        Account account = accountsManager.getAccountById(userId);

        accountsManager.deleteAccount(account.getAccountId());
    }
}
