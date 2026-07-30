package com.tms.businessservice.exception;

/**
 * Thrown when submitted data is correctly formatted but breaks a business
 * rule—for example, assigning an EMPLOYEE as a Project Manager.
 */
public class BusinessValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessValidationException(String message) {
        super(message);
    }
}
