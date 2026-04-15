package com.hotelbooking.exception;

public class UnauthorizedActionException extends SecurityException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
