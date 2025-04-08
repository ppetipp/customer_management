package com.parpet.customer_management.config;

import com.parpet.customer_management.audit.PersistentAuditEventRepository;
import com.parpet.customer_management.audit.repository.AuditEventEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditingConfiguration {
    private final AuditEventEntityRepository auditEventEntityRepository;

    @Autowired
    public AuditingConfiguration(AuditEventEntityRepository auditEventEntityRepository) {
        this.auditEventEntityRepository = auditEventEntityRepository;
    }

    @Bean
    public PersistentAuditEventRepository auditEventRepository() {
        return new PersistentAuditEventRepository(auditEventEntityRepository);
    }
}
