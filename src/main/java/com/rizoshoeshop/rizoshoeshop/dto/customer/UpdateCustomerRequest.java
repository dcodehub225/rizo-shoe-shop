package com.rizoshoeshop.rizoshoeshop.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.PastOrPresent; // Ensures DOB is not in the future
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class UpdateCustomerRequest {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    private String address;

    private String city;

    private LocalDate dob;

    @NotBlank(message = "Loyalty level cannot be blank")
    private String loyaltyLevel;

    // Loyalty points update will likely be handled by Sales Service or a separate endpoint
    // private int loyaltyPoints;
}