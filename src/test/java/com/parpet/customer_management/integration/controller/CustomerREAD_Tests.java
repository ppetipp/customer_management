package com.parpet.customer_management.integration.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parpet.customer_management.model.Customer;
import com.parpet.customer_management.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerREAD_Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        customerRepository.deleteAll();
    }

    @Test
    void getCustomers_WithDefaultPagination_ShouldReturnFirstTenCustomers() throws Exception {
        // Given
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            Customer customer = new Customer();
            customer.setName("Test Customer " + i);
            customer.setAge(20 + i);
            customer.setDateOfBirth(LocalDate.of(1990, 1, i));
            customer.setGender("M");
            customers.add(customer);
        }
        customerRepository.saveAll(customers);

        // When & Then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getCustomers_WithCustomPagination_ShouldReturnRequestedPage() throws Exception {
        // Given
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Customer customer = new Customer();
            customer.setName("Test Customer " + i);
            customer.setAge(20 + i);
            customer.setDateOfBirth(LocalDate.of(1990, 1, i));
            customer.setGender("M");
            customers.add(customer);
        }
        customerRepository.saveAll(customers);

        // When & Then
        mockMvc.perform(get("/api/customers")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Customer 4"))
                .andExpect(jsonPath("$.content[1].name").value("Test Customer 5"));
    }
} 