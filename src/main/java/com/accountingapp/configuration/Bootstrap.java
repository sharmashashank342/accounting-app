package com.accountingapp.configuration;

import com.accountingapp.server.HttpServer;
import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Date;

public class Bootstrap {

    private static long processId;

    private static Logger log = Logger.getLogger(Bootstrap.class);

    static {

        try {
            // Getting PID for Current Running Application
            String processInfo = ManagementFactory.getRuntimeMXBean().getName();
            int index = processInfo.indexOf('@');
            String pid = processInfo.substring(0, index);
            processId = Long.parseLong(pid);
        } catch (Exception e) {
            processId = 1L;
        }
    }

    private Bootstrap() {
    }

    public static void initializeApp() throws Exception {

        initilazeContext();

        ensureTestData();

        startServer();
    }

    private static void ensureTestData() {
        log.info("Populating Test Data");

        Context.getContext().getDbManager().populateData();

        log.info("Data Initialisation Complete....");
    }

    private static void startServer() throws Exception {

        // Host service on jetty
        HttpServer.start();
    }

    private static void initilazeContext() {
        log.info("Init Context");

        Context.initilazeContext();

        log.info("Created Context at "+new Date());
    }

    public static long getProcessId() {
        return processId;
    }
}
