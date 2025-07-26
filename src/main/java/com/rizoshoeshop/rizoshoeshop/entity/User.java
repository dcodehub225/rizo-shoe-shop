package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set; // For roles/authorities

@Entity // Marks this class as a JPA entity
@Table(name = "users") // Specifies the table name for login users
@Data // Lombok: Generates getters, setters, toString, etc.
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all arguments
public class User {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false, unique = true) // Username must be unique
    private String username;

    @Column(nullable = false)
    private String password; // Encrypted password

    @ElementCollection(fetch = FetchType.EAGER) // Roles are eagerly fetched
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles; // e.g., "ROLE_ADMIN", "ROLE_USER"

    // Optional: Link to Employee entity if user is an employee
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "employee_id", unique = true)
    // private Employee employee;
}