package com.assignment.exception;

public class InvalidAccountIdException extends InvalidRequestException {

    public InvalidAccountIdException(long accountId) {
        super("Invalid Account Id "+accountId);
    }

    public InvalidAccountIdException(long accountId, int statusCode) {
        super("Invalid Account Id "+accountId, statusCode);
    }
}
