package com.assignment.events.listener;

import com.assignment.controller.RestController;
import com.assignment.data.managers.AccountsManager;
import com.assignment.events.Domain;
import com.assignment.events.EventDTO;
import com.assignment.model.Account;

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

            switch (eventDTO.getEventType()) {
                case USER_DEACTIVATED:
                    deactivateUserAccount(Long.valueOf(eventDTO.getEventParam().toString()));

                    // Set for Further Use if any
                    eventDTO.setHandled(true);
            }
        }
    }

    private void deactivateUserAccount(Long userId) {

        Account account = accountsManager.getAccountById(userId);

        accountsManager.deleteAccount(account.getAccountId());

        // Mark User as fully Inactivated
        RestController.removeInactiveUser(userId);
    }
}
