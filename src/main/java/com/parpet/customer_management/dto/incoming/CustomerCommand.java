package com.parpet.customer_management.dto.incoming;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CustomerCommand {
    @NotBlank(message = "A név megadása kötelező")
    @Size(max = 100, message = "A név hossza nem haladhatja meg a 100 karaktert")
    private String name;

    @Positive(message = "Az életkor pozitív szám kell legyen")
    private Integer age;

    @NotNull(message = "A születési dátum megadása kötelező")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 200, message = "Az cím hossza nem haladhatja meg a 200 karaktert")
    private String address;

    @Pattern(regexp = "^[MF]$")
    private String gender;
}