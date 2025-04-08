package com.parpet.customer_management.integration.controller;


import com.parpet.customer_management.model.Customer;
import com.parpet.customer_management.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerDELETE_Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Teszt adat létrehozása
        testCustomer = new Customer();
        testCustomer.setName("Test Customer");
        testCustomer.setAge(30);
        testCustomer.setDateOfBirth(LocalDate.of(1994, 1, 1));
        testCustomer.setGender("M");
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    void deleteCustomer_WhenCustomerExists_ShouldDeleteAndReturnNoContent() throws Exception {
        // Given
        Long customerId = testCustomer.getId();
        assertTrue(customerRepository.existsById(customerId), "Customer should exist before deletion");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{id}", customerId))
                .andExpect(status().isNoContent());

        // Verify customer was deleted
        assertFalse(customerRepository.existsById(customerId), "Customer should not exist after deletion");
    }

    @Test
    void deleteCustomer_WhenCustomerDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        Long nonExistentCustomerId = 99999L;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{id}", nonExistentCustomerId))
                .andExpect(status().isNotFound());
    }
} 