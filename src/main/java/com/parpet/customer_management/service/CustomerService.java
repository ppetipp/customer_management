package com.parpet.customer_management.service;

import com.parpet.customer_management.dto.incoming.CustomerCommand;
import com.parpet.customer_management.dto.incoming.QueryDto;
import com.parpet.customer_management.dto.incoming.SortDto;
import com.parpet.customer_management.model.Customer;
import com.parpet.customer_management.repository.CustomerRepository;
import com.parpet.customer_management.util.JsonUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // CREATE
    public Customer createCustomer(CustomerCommand customerCommand) {
        Customer customer = new Customer();
        updateCustomerFromDto(customer, customerCommand);
        customer = customerRepository.save(customer);

        return customer;
    }

    // READ
    public Page<Customer> getCustomers(QueryDto queryDto) {
        // parse and create sort orders
        List<SortDto> sortDtos = JsonUtils.jsonStringToSortDto(queryDto.getSort());
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
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        updateCustomerFromDto(customer, customerCommand);
        customer = customerRepository.save(customer);

        return customer;
    }

    // DELETE
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }

        customerRepository.deleteById(id);
    }

    private void updateCustomerFromDto(Customer customer, CustomerCommand dto) {
        customer.setName(dto.getName());
        customer.setAge(dto.getAge());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
    }
}