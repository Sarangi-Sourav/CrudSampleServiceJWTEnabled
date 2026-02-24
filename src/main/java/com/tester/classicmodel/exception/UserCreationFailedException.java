package com.tester.classicmodel.exception;

public class UserCreationFailedException extends RuntimeException {
    public UserCreationFailedException(String message, String exMessage) {
        super(message + exMessage);
    }
}
