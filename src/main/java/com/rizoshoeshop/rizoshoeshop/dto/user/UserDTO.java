package com.rizoshoeshop.rizoshoeshop.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class UserDTO {
    private Long id;
    private String username;
    private Set<String> roles;
    // Do NOT include password here
}