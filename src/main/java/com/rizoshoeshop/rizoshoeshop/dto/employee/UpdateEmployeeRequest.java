package com.rizoshoeshop.rizoshoeshop.dto.employee; // Updated package name

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class UpdateEmployeeRequest {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    private String city;

    @NotNull(message = "Date of Joining cannot be null")
    @PastOrPresent(message = "Date of Joining cannot be in the future")
    private LocalDate dateOfJoining;

    @NotBlank(message = "Branch cannot be blank")
    private String branch;

    @NotBlank(message = "Role cannot be blank")
    private String role;
}