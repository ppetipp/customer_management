package com.parpet.customer_management.audit;

import com.parpet.customer_management.audit.model.AuditEventEntity;
import com.parpet.customer_management.audit.repository.AuditEventEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@Slf4j
public class PersistentAuditEventRepository implements AuditEventRepository {
    private final AuditEventEntityRepository auditEventEntityRepository;

    @Autowired
    public PersistentAuditEventRepository(AuditEventEntityRepository auditEventEntityRepository) {
        this.auditEventEntityRepository = auditEventEntityRepository;
    }

    @Override
    @Transactional
    public void add(AuditEvent event) {
        try {
            AuditEventEntity entity = new AuditEventEntity();
            entity.setPrincipal(event.getPrincipal());
            entity.setType(event.getType());
            entity.setTimestamp(event.getTimestamp());
            entity.setData(event.getData());

            auditEventEntityRepository.save(entity);
            log.info("Audit event saved successfully: {}", event.getType());
        } catch (Exception e) {
            log.error("Failed to save audit event: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> find(String principal, Instant after, String type) {
        return auditEventEntityRepository.findAuditEvents(principal, after, type)
                .stream()
                .map(this::convertToAuditEvent)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AuditEvent> find_ordered(Pageable pageable) {
        List<AuditEvent> auditEvents = auditEventEntityRepository.findAll(pageable)
                .stream()
                .map(this::convertToAuditEvent)
                .collect(Collectors.toList());
        return new PageImpl<>(auditEvents, pageable, auditEventEntityRepository.count());
    }

    private AuditEvent convertToAuditEvent(AuditEventEntity entity) {
        return new AuditEvent(
                entity.getTimestamp(),
                entity.getPrincipal(),
                entity.getType(),
                entity.getData()
        );
    }
} 