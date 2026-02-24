package com.tester.classicmodel.exception;

/**
 * Exception thrown when a requested resource is not found in the database.
 * This typically results in a 404 HTTP status code.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s with ID %s not found", resourceType, id));
    }
}