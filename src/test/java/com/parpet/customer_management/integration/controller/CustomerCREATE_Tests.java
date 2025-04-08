package com.parpet.customer_management.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parpet.customer_management.dto.incoming.CustomerCommand;
import com.parpet.customer_management.dto.outgoing.CustomerDetails;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerCREATE_Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        customerRepository.deleteAll();
    }

    @Test
    void createCustomer_WithValidData_ShouldReturnCreatedCustomer() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("Test Customer");
        command.setAge(30);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));
        command.setAddress("Test Address");
        command.setGender("M");

        // When
        MvcResult result = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CustomerDetails customerResult = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerDetails.class
        );

        assertNotNull(customerResult.getId());
        assertEquals(command.getName(), customerResult.getName());
        assertEquals(command.getAge(), customerResult.getAge());
        assertEquals(command.getDateOfBirth(), customerResult.getDateOfBirth());
        assertEquals(command.getAddress(), customerResult.getAddress());
        assertEquals(command.getGender(), customerResult.getGender());

        // Verify data in repository
        assertTrue(customerRepository.findById(customerResult.getId()).isPresent());
    }

    @Test
    void createCustomer_WithNullName_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName(null);
        command.setAge(30);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomer_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("");
        command.setAge(30);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomer_WithTooLongName_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("a".repeat(101));
        command.setAge(30);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomer_WithNegativeAge_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("Test Customer");
        command.setAge(-1);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomer_WithNullDateOfBirth_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("Test Customer");
        command.setAge(30);
        command.setDateOfBirth(null);

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomer_WithTooLongAddress_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("Test Customer");
        command.setAge(30);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));
        command.setAddress("a".repeat(201));

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomer_WithInvalidGender_ShouldReturnBadRequest() throws Exception {
        // Given
        CustomerCommand command = new CustomerCommand();
        command.setName("Test Customer");
        command.setAge(30);
        command.setDateOfBirth(LocalDate.of(1994, 1, 1));
        command.setGender("X");

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        // Verify no data was saved
        assertEquals(0, customerRepository.count());
    }
} 