package com.parpet.customer_management.audit;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class CustomAuditEventRepository implements AuditEventRepository {
    private final ConcurrentLinkedQueue<AuditEvent> auditEvents = new ConcurrentLinkedQueue<>();
    private static final int MAX_EVENTS = 1000;

    @Override
    public void add(AuditEvent event) {
        if (auditEvents.size() >= MAX_EVENTS) {
            auditEvents.poll();
        }
        auditEvents.add(event);
    }

    @Override
    public List<AuditEvent> find(String principal, Instant after, String type) {
        List<AuditEvent> result = new ArrayList<>();
        for (AuditEvent event : auditEvents) {
            if (matches(event, principal, after, type)) {
                result.add(event);
            }
        }
        return result;
    }

    private boolean matches(AuditEvent event, String principal, Instant after, String type) {
        if (principal != null && !principal.equals(event.getPrincipal())) {
            return false;
        }
        if (after != null && event.getTimestamp().isBefore(after)) {
            return false;
        }
        if (type != null && !type.equals(event.getType())) {
            return false;
        }
        return true;
    }
}