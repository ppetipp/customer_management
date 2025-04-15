package com.parpet.customer_management.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parpet.customer_management.audit.dto.CustomerAuditEventCommand;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CustomerAuditAspect {

    private final CustomerAuditEventPublisher auditEventPublisher;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.parpet.customer_management.controller.CustomerController.*(..))")
    public Object auditCustomerOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        try {
            Object result = joinPoint.proceed();
            publishSuccessAudit(methodName, args);
            return result;
        } catch (EntityNotFoundException ex) {
            publishNotFoundError(methodName, extractCustomerId(args));
            throw ex;
        } catch (Exception ex) {
            if (!(ex instanceof MethodArgumentNotValidException)) {
                publishGeneralError(methodName, args);
            }
            throw ex;
        }
    }

    private void publishSuccessAudit(String methodName, Object[] args) {
        try {
            String operation = determineOperation(methodName);
            Long customerId = extractCustomerId(args);
            String request = args.length > 0 ? objectMapper.writeValueAsString(args[0]) : null;

            auditEventPublisher.publishAuditEvent(CustomerAuditEventCommand.builder()
                    .action(operation)
                    .customerId(customerId)
                    .request(request)
                    .status("SUCCESS")
                    .timestamp(Instant.now())
                    .build());
        } catch (Exception ex) {
            log.error("Error publishing success audit: ", ex);
        }
    }

    private Long extractCustomerId(Object[] args) {
        if (args.length > 0 && args[0] instanceof Long) {
            return (Long) args[0];
        }
        return null;
    }

    private String determineOperation(String methodName) {
        return switch (methodName) {
            case "createCustomer" -> "CREATE_CUSTOMER";
            case "updateCustomer" -> "UPDATE_CUSTOMER";
            case "deleteCustomer" -> "DELETE_CUSTOMER";
            default -> "UNKNOWN_OPERATION";
        };
    }

    private void publishNotFoundError(String methodName, Long aLong) {
        try {
            String operation = determineOperation(methodName);

            auditEventPublisher.publishAuditEvent(CustomerAuditEventCommand.builder()
                    .action(operation)
                    .customerId(aLong)
                    .request(aLong.toString())
                    .status("NOT_FOUND")
                    .timestamp(Instant.now())
                    .build());
        } catch (Exception ex) {
            log.error("Error publishing not found error: ", ex);
        }
    }

    private void publishGeneralError(String methodName, Object[] args) {
        try {
            String operation = determineOperation(methodName);
            Long customerId = extractCustomerId(args);
            String request = args.length > 0 ? objectMapper.writeValueAsString(args[0]) : null;

            auditEventPublisher.publishAuditEvent(CustomerAuditEventCommand.builder()
                    .action(operation)
                    .customerId(customerId)
                    .request(request)
                    .status("GENERAL_ERROR")
                    .timestamp(Instant.now())
                    .build());
        } catch (Exception ex) {
            log.error("Error publishing general error: ", ex);
        }
    }
}
