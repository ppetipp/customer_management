package com.parpet.customer_management.audit;

import com.parpet.customer_management.dto.incoming.QueryDto;
import com.parpet.customer_management.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class CustomAuditService {
    private final PersistentAuditEventRepository persistentAuditEventRepository;

    public CustomAuditService(PersistentAuditEventRepository persistentAuditEventRepository) {
        this.persistentAuditEventRepository = persistentAuditEventRepository;
    }

    public Page<AuditEvent> getAuditEvents(QueryDto queryDto) {
        // parse and create sort orders
        PageRequest pageRequest = JsonUtils.jsonStringToPageRequest(queryDto);

        return persistentAuditEventRepository.find_ordered(pageRequest);
    }
}
