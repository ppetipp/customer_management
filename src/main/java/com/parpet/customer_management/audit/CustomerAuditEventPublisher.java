package com.parpet.customer_management.audit;

import com.parpet.customer_management.audit.dto.CustomerAuditEventCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerAuditEventPublisher {
    private final AuditEventRepository auditEventRepository;

    @Autowired
    public CustomerAuditEventPublisher(@Qualifier("persistentAuditEventRepository")AuditEventRepository auditEventRepository){
        this.auditEventRepository = auditEventRepository;
    }

    public void publishAuditEvent(CustomerAuditEventCommand customerAuditEventCommand) {
        Map<String, Object> data = new HashMap<>();
        data.put("customerId", customerAuditEventCommand.getCustomerId());
        data.put("request", customerAuditEventCommand.getRequest());
        data.put("status", customerAuditEventCommand.getStatus());

        AuditEvent auditEvent = new AuditEvent(
                customerAuditEventCommand.getTimestamp(),
                "SYSTEM",
                customerAuditEventCommand.getAction(),
                data
        );

        auditEventRepository.add(auditEvent);
    }
} 