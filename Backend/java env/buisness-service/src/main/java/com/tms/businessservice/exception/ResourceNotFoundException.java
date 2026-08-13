package com.tms.businessservice.exception;

/**
 * Thrown when an API requests a database record that does not exist.
 *
 * A global exception handler added with the REST controller will translate
 * this exception into an HTTP 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
