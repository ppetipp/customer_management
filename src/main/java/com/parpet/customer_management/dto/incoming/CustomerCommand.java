package com.parpet.customer_management.dto.incoming;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CustomerCommand {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Positive
    private Integer age;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 200)
    private String address;

    @Pattern(regexp = "^[MF]$")
    private String gender;
}