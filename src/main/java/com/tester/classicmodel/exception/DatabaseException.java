package com.tester.classicmodel.exception;

/**
 * Exception thrown when database operations fail.
 * This includes connection errors, constraint violations, and other database-related issues.
 */
public class DatabaseException extends RuntimeException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}