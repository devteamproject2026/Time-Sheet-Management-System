package com.tms.businessservice.exception;

/**
 * Thrown when a request conflicts with data already stored in the database.
 *
 * Example: assigning the same Employee to the same Project twice.
 */
public class ResourceConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceConflictException(String message) {
        super(message);
    }
}
