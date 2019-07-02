package com.assignment.utils;

import com.assignment.configuration.Bootstrap;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;


public class Utils {

    private static final AtomicLong atomicLong = new AtomicLong(1);

    private static Properties properties = new Properties();

    private static final Logger log = Logger.getLogger(Utils.class);

    private static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    //initialise

    static {
        String configFileName = System.getProperty("application.properties");

        if (configFileName == null) {
            configFileName = "application.properties";
        }
        loadConfig(configFileName);

    }

    private static void loadConfig(String fileName) {
        if (fileName == null) {
            log.warn("config file name cannot be null");
        } else {
            try {
                log.info("Loading config file: " + fileName);
                final InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                properties.load(fis);
            } catch (FileNotFoundException fne) {
                log.error("Config file not found " + fileName, fne);
            } catch (IOException ioe) {
                log.error("error reading Config file  " + fileName, ioe);
            }
        }

    }


    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }

    public static boolean isNotValid(String email) {
        return !email.matches(EMAIL_REGEX);
    }

    public static String getNewTransactionId() {

        // Adding too much Randomness in transactionId's
        // TODO: Find alternative to create txn id
        long txnId = atomicLong.getAndIncrement() + Bootstrap.getProcessId() +
                (System.nanoTime() - System.currentTimeMillis());

        return String.valueOf(txnId);
    }
}
