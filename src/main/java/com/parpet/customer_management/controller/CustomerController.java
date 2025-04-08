package com.parpet.customer_management.controller;

import com.parpet.customer_management.dto.incoming.CustomerCommand;
import com.parpet.customer_management.dto.incoming.CustomerQueryDto;
import com.parpet.customer_management.dto.mapper.CustomerMapper;
import com.parpet.customer_management.dto.outgoing.CustomerDetails;
import com.parpet.customer_management.model.Customer;
import com.parpet.customer_management.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerCommand customerCommand) {
        Customer result = customerService.createCustomer(customerCommand);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDetails>> getCustomers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"field\":\"name\",\"direction\":\"ASC\"}]") String sort
    ) {
        Page<Customer> customers = customerService.getCustomers(CustomerQueryDto.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .build());

        return new ResponseEntity<>(customers.map(customerMapper::entitiesToDto), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerCommand customerCommand) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerCommand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
} 