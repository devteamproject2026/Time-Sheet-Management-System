package com.tms.businessservice.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Standard JSON structure returned when a Business Service API fails.
 *
 * validationErrors is empty for normal errors and contains field-specific
 * messages when request validation fails.
 */
@Getter
@AllArgsConstructor
@Builder
public class ApiErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private Map<String, String> validationErrors;
}
