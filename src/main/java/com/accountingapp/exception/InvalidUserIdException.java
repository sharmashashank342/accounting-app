package com.accountingapp.exception;

public class InvalidUserIdException extends InvalidRequestException {

    public InvalidUserIdException(long userId) {
        super("Invalid User Id "+userId);
    }

    public InvalidUserIdException(long userId, int statusCode) {
        super("Invalid User Id "+userId, statusCode);
    }
}
