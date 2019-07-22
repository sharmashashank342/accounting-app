package com.accountingapp.configuration;

import com.accountingapp.data.DBManager;
import com.accountingapp.data.H2DBManager;
import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.data.managers.UserManager;
import com.accountingapp.events.listener.UserEventListener;
import com.accountingapp.events.publisher.EventPublisher;
import com.accountingapp.events.service.EventService;
import com.accountingapp.events.service.EventServiceImpl;
import com.accountingapp.service.AccountService;
import com.accountingapp.service.UserService;
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
        accountsManager = new AccountsManager();
        userManager = new UserManager();
        accountService = new AccountService(accountsManager);
        userService = new UserService(userManager);

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
