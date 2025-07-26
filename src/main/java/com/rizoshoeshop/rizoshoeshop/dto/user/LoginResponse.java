package com.rizoshoeshop.rizoshoeshop.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class LoginResponse {
    private String jwtToken;
    private String username;
    private String role; // Main role or a list of roles
}