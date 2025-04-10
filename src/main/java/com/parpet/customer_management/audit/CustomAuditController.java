package com.parpet.customer_management.audit;

import com.parpet.customer_management.dto.incoming.QueryDto;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actuator/auditevents/pageable")
public class CustomAuditController {
    private final CustomAuditService customAuditService;

    public CustomAuditController(CustomAuditService customAuditService) {
        this.customAuditService = customAuditService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditEvent>> getCustomers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "100") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"field\":\"timestamp\",\"direction\":\"DESC\"}]") String sort
    ) {
        Page<AuditEvent> auditEvents = customAuditService.getAuditEvents(QueryDto.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .build());

        return new ResponseEntity<>(auditEvents, HttpStatus.OK);
    }
}
