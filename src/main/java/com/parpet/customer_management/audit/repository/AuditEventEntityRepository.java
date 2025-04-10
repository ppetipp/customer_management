package com.parpet.customer_management.audit.repository;

import com.parpet.customer_management.audit.model.AuditEventEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AuditEventEntityRepository extends JpaRepository<AuditEventEntity, Long> {

    @Query("SELECT a FROM AuditEventEntity a ORDER BY a.timestamp DESC LIMIT 100")
    List<AuditEventEntity> findAuditEvents(
            @Param("principal") String principal,
            @Param("after") Instant after,
            @Param("type") String type
    );

    List<AuditEventEntity> findAll(Sort sort);
} 