package com.parpet.customer_management.audit.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class CustomerAuditEventCommand {
    private String action;
    private Long customerId;
    private String request;
    private String status;
    private Instant timestamp;
} 