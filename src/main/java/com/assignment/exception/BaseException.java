package com.assignment.exception;


public class BaseException extends RuntimeException {

    private int statusCode = 500;

    // Internal Service Exceptions
    public BaseException(String msg, Throwable throwable, int statusCode) {
        super(msg, throwable);
        this.statusCode = statusCode;
    }

    public BaseException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
