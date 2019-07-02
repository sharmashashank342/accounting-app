package com.assignment.configuration;

import com.assignment.data.DBManager;
import com.assignment.data.H2DBManager;
import com.assignment.data.managers.AccountsManager;
import com.assignment.data.managers.UserManager;
import com.assignment.events.listener.UserEventListener;
import com.assignment.events.publisher.EventPublisher;
import com.assignment.events.service.EventService;
import com.assignment.events.service.EventServiceImpl;
import com.assignment.service.AccountService;
import com.assignment.service.AccountServiceImpl;
import com.assignment.service.UserService;
import com.assignment.service.UserServiceImpl;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.util.Arrays;

@Getter
public class Context {

    private static Logger logger = Logger.getLogger(Context.class.getName());

    private static Context context;

    private final DBManager dbManager;

    private final AccountsManager accountsManager;

    private final UserManager userManager;

    private final AccountService accountService;

    private final UserService userService;

    // In App Event Publisher
    private final EventPublisher eventPublisher;

    // In App Event Listener,
    private final UserEventListener userEventListener;

    private final EventService eventService;

    private final EventExecutor eventExecutor;

    protected Context() {
        context = this;
        dbManager = new H2DBManager();
        accountsManager = dbManager.getAccountsManager();
        userManager = dbManager.getUserManager();
        accountService = new AccountServiceImpl(accountsManager);
        userService = new UserServiceImpl(userManager);

        userEventListener = new UserEventListener(accountsManager);
        // can register more events to publisher
        eventPublisher = new EventPublisher(Arrays.asList(userEventListener));

        eventExecutor = new EventExecutor();
        eventService = new EventServiceImpl(eventPublisher, eventExecutor);
    }

    static void initilazeContext() {
        if (context == null)
            context = new Context();
    }

    public static Context getContext() {
        return context;
    }
}
