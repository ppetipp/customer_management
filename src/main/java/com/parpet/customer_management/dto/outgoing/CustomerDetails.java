package com.parpet.customer_management.dto.outgoing;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerDetails {
    private String name;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String address;

    private String gender;
}
