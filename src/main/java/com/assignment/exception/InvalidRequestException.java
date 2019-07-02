package com.assignment.exception;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(String msg) {
        super(msg, 400);
    }

    public InvalidRequestException(String msg, int statusCode) {
        super(msg, statusCode);
    }
}
