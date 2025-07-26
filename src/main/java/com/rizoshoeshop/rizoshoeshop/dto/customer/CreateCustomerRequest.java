package com.rizoshoeshop.rizoshoeshop.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull; // Optional, depends on DOB being mandatory
// import jakarta.validation.constraints.PastOrPresent; // Ensures DOB is not in the future
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class CreateCustomerRequest {

    @NotBlank(message = "First name cannot be blank") // Validation: field must not be null and must contain at least one non-whitespace character
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format") // Validation: field must be a valid email address
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    private String address;

    private String city;

    private LocalDate dob; // Date of Birth

    @NotBlank(message = "Loyalty level cannot be blank")
    private String loyaltyLevel; // Default can be set in service if not provided
}