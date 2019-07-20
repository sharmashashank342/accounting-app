package com.accountingapp.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventExecutor {

    private Executor executor;

    EventExecutor() {
        executor = Executors.newFixedThreadPool(5);
    }

    public void executeRunnable(Runnable runnable) {

        executor.execute(runnable);
    }
}
