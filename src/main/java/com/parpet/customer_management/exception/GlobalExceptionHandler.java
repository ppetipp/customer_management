package com.parpet.customer_management.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parpet.customer_management.audit.CustomerAuditEventPublisher;
import com.parpet.customer_management.audit.dto.CustomerAuditEventCommand;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;
    private final CustomerAuditEventPublisher auditEventPublisher;
    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        try {
            // Kérés adatainak kinyerése
            Object requestBody = ex.getBindingResult().getTarget();
            String requestJson = objectMapper.writeValueAsString(requestBody);

            // Művelet típusának meghatározása
            String methodName = Objects.requireNonNull(ex.getParameter().getMethod()).getDeclaringClass().getName();
            String operation = determineOperation(methodName);

            // Audit esemény létrehozása
            auditEventPublisher.publishAuditEvent(CustomerAuditEventCommand.builder()
                    .action(operation)
                    .customerId(null)
                    .request(requestJson)
                    .status("VALIDATION_ERROR")
                    .timestamp(Instant.now())
                    .build());

            // Hibaüzenet összeállítása
            Map<String, Object> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));

            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            log.error("Error handling validation exception", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Validation error occurred"));
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        try {
            // Audit esemény létrehozása
            auditEventPublisher.publishAuditEvent(CustomerAuditEventCommand.builder()
                    .action("CUSTOMER_OPERATION")
                    .customerId(null)
                    .request(ex.getMessage())
                    .status("VALIDATION_ERROR")
                    .timestamp(Instant.now())
                    .build());

            // Hibaüzenet összeállítása
            Map<String, Object> errors = new HashMap<>();
            ex.getConstraintViolations().forEach(violation ->
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            log.error("Error handling constraint violation", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Validation error occurred"));
        }
    }

    private String determineOperation(String methodName) {
        return switch (methodName) {
            case "createCustomer" -> "CREATE_CUSTOMER";
            case "updateCustomer" -> "UPDATE_CUSTOMER";
            case "deleteCustomer" -> "DELETE_CUSTOMER";
            default -> "UNKNOWN_OPERATION";
        };
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        logger.error("Request JSON could no be parsed: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.JSON_PARSE_ERROR.name(), "The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.ILLEGAL_ARGUMENT_ERROR.name(), "An illegal argument has been passed to the method.", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex) {
        logger.error("IllegalStateException occurred: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.ILLEGAL_STATE_EXCEPTION.name(), "An illegal state has occurred.", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex) {
        logger.error("No such element: ", ex);
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiError body = new ApiError(ERROR_CODE.NO_SUCH_ELEMENT.name(), "There is no such element", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entity not found: ", ex);
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiError body = new ApiError(ERROR_CODE.ENTITY_NOT_FOUND.name(), "Entity not found", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {
        logger.error("An unexpected error occurred: ", t);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (t instanceof Error) {
            System.exit(1);
        }
        ApiError body = new ApiError(ERROR_CODE.UNCLASSIFIED_ERROR.name(), "Oh, snap! Something really unexpected occurred.", t.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Error: input data missing. ", ex);
        ApiError body = new ApiError(ERROR_CODE.ILLEGAL_ARGUMENT_ERROR.name(), "Error: input data missing.", "");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}

