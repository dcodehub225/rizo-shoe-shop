package com.rizoshoeshop.rizoshoeshop.dto.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {

    @NotBlank(message = "Supplier name cannot be blank")
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Contact person cannot be blank")
    private String contactPerson;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    private String city;
    private String country;
    private boolean isInternational;
}