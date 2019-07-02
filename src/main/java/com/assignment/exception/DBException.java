package com.assignment.exception;

public class DBException extends BaseException {

    public DBException(String msg) {
        super(msg, 500);
    }

    public DBException(String msg, int statusCode) {
        super(msg, statusCode);
    }

    public DBException(String msg, Throwable throwable) {
        super(msg, throwable, 500);
    }
}
