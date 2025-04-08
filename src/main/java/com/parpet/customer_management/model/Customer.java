package com.parpet.customer_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Positive
    private Integer age;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @Size(max = 200)
    private String address;

    @Pattern(regexp = "^[MF]$")
    private String gender;
} 