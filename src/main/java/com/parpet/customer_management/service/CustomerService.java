package com.parpet.customer_management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parpet.customer_management.audit.CustomerAuditEventPublisher;
import com.parpet.customer_management.audit.dto.CustomerAuditEventCommand;
import com.parpet.customer_management.dto.incoming.CustomerCommand;
import com.parpet.customer_management.dto.incoming.CustomerQueryDto;
import com.parpet.customer_management.dto.incoming.SortDto;
import com.parpet.customer_management.model.Customer;
import com.parpet.customer_management.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerAuditEventPublisher auditEventPublisher;


    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerAuditEventPublisher auditEventPublisher) {
        this.customerRepository = customerRepository;
        this.auditEventPublisher = auditEventPublisher;
    }

    // CREATE
    public Customer createCustomer(CustomerCommand customerCommand) {
        try {
            Customer customer = new Customer();
            updateCustomerFromDto(customer, customerCommand);
            customer = customerRepository.save(customer);
            publishAudit("CREATE_CUSTOMER", customer.getId(), customerCommand.toString(), "SUCCESS");
            return customer;
        } catch (Exception e) {
            publishAudit("CREATE_CUSTOMER", null, customerCommand.toString(), "FAILED");
            throw e;
        }
    }

    // READ
    public Page<Customer> getCustomers(CustomerQueryDto queryDto) {
        // parse and create sort orders
        List<SortDto> sortDtos = jsonStringToSortDto(queryDto.getSort());
        List<Sort.Order> orders = new ArrayList<>();

        if (sortDtos != null) {
            for (SortDto sortDto : sortDtos) {
                Sort.Direction direction = Objects.equals(sortDto.getDirection(), "desc")
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, sortDto.getField()));
            }
        }

        // Create page request with sorting
        PageRequest pageRequest = PageRequest.of(
                queryDto.getPage(),
                queryDto.getSize(),
                Sort.by(orders));

        return customerRepository.findAll(pageRequest);
    }

    // UPDATE
    public Customer updateCustomer(Long id, CustomerCommand customerCommand) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
            updateCustomerFromDto(customer, customerCommand);
            customer = customerRepository.save(customer);

            publishAudit("UPDATE_CUSTOMER", id, customerCommand.toString(), "SUCCESS");
            return customer;
        } catch (Exception e) {
            publishAudit("UPDATE_CUSTOMER", id, customerCommand.toString(), "FAILED");
            throw e;
        }
    }

    // DELETE
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            publishAudit("DELETE_CUSTOMER", id, null, "FAILED");
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }
        try {
            customerRepository.deleteById(id);
            publishAudit("DELETE_CUSTOMER", id, null, "SUCCESS");
        } catch (Exception e) {
            publishAudit("DELETE_CUSTOMER", id, null, "FAILED");
            throw e;
        }
    }

    private List<SortDto> jsonStringToSortDto(String jsonString) {
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.readValue(jsonString, obj.getTypeFactory().constructCollectionType(List.class, SortDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON string", e);
        }
    }

    private void updateCustomerFromDto(Customer customer, CustomerCommand dto) {
        customer.setName(dto.getName());
        customer.setAge(dto.getAge());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
    }

    private void publishAudit(String action, Long customerId, String request, String status) {
        CustomerAuditEventCommand auditEvent = CustomerAuditEventCommand.builder()
                .action(action)
                .customerId(customerId)
                .request(request)
                .status(status)
                .timestamp(Instant.now())
                .build();

        auditEventPublisher.publishAuditEvent(auditEvent);
    }
} 