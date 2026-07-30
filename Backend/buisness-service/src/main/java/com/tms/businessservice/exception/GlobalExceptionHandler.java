package com.tms.businessservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts Java exceptions into clear HTTP/JSON responses for all controllers.
 *
 * This keeps exception formatting in one place instead of repeating try/catch
 * blocks inside every API method.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Converts a duplicate or conflicting request into HTTP 409 Conflict.
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceConflict(
            ResourceConflictException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                Map.of());
    }

    /**
     * MySQL blocks deletion when another protected record still references the
     * selected row. HTTP 409 explains that database relationship conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation() {

        return buildResponse(
                HttpStatus.CONFLICT,
                "This record cannot be deleted because other records depend on it",
                Map.of());
    }

    /**
     * Converts a broken business rule into HTTP 400 Bad Request.
     *
     * Examples include an invalid user role or an end date before a start date.
     */
    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessValidation(
            BusinessValidationException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                Map.of());
    }

    /**
     * @PreAuthorize throws this exception when a logged-in user has the wrong
     * role. Returning 403 clearly means "logged in, but not permitted".
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied() {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                "You are not authorized to perform this operation",
                Map.of());
    }

    /**
     * Converts a missing database record into HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                Map.of());
    }

    /**
     * Converts @Valid DTO failures into HTTP 400 and returns each invalid field.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                fieldErrors);
    }

    /**
     * Handles malformed JSON, such as a missing quote or invalid JSON syntax.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableJson() {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body contains invalid JSON",
                Map.of());
    }

    /**
     * Final safety handler for unexpected server-side failures.
     *
     * Internal exception details are not returned to API users because they can
     * expose database or implementation information.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException() {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal server error occurred",
                Map.of());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> validationErrors) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
