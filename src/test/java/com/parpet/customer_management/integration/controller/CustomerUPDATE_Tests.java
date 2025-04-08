package com.parpet.customer_management.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parpet.customer_management.dto.incoming.CustomerCommand;
import com.parpet.customer_management.dto.outgoing.CustomerDetails;
import com.parpet.customer_management.model.Customer;
import com.parpet.customer_management.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerUPDATE_Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    private ObjectMapper objectMapper;
    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        customerRepository.deleteAll();

        // Teszt customer létrehozása
        existingCustomer = new Customer();
        existingCustomer.setName("Original Name");
        existingCustomer.setAge(30);
        existingCustomer.setDateOfBirth(LocalDate.of(1994, 1, 1));
        existingCustomer.setGender("M");
        existingCustomer.setAddress("Original Address");
        existingCustomer = customerRepository.save(existingCustomer);
    }

    @Test
    void updateCustomer_WithValidData_ShouldReturnUpdatedCustomer() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("Updated Name");
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));
        updateCommand.setGender("F");
        updateCommand.setAddress("Updated Address");

        // When
        MvcResult result = mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CustomerDetails updatedCustomer = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerDetails.class
        );

        assertEquals(existingCustomer.getId(), updatedCustomer.getId());
        assertEquals(updateCommand.getName(), updatedCustomer.getName());
        assertEquals(updateCommand.getAge(), updatedCustomer.getAge());
        assertEquals(updateCommand.getDateOfBirth(), updatedCustomer.getDateOfBirth());
        assertEquals(updateCommand.getGender(), updatedCustomer.getGender());
        assertEquals(updateCommand.getAddress(), updatedCustomer.getAddress());
    }

    @Test
    void updateCustomer_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
        // Given
        Long nonExistentId = 99999L;
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("Updated Name");
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCustomer_WithNullName_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName(null);
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());

        // Verify original data unchanged
        Customer unchangedCustomer = customerRepository.findById(existingCustomer.getId()).orElseThrow();
        assertEquals("Original Name", unchangedCustomer.getName());
    }

    @Test
    void updateCustomer_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("");
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_WithTooLongName_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("a".repeat(101));
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_WithNegativeAge_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("Updated Name");
        updateCommand.setAge(-1);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_WithNullDateOfBirth_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("Updated Name");
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(null);

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_WithTooLongAddress_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("Updated Name");
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));
        updateCommand.setAddress("a".repeat(201));

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_WithInvalidGender_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand updateCommand = new CustomerCommand();
        updateCommand.setName("Updated Name");
        updateCommand.setAge(35);
        updateCommand.setDateOfBirth(LocalDate.of(1989, 1, 1));
        updateCommand.setGender("X");

        // When & Then
        mockMvc.perform(put("/api/customers/{id}", existingCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isBadRequest());
    }
} 