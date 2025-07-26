package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity // Marks this class as a JPA entity, mapping it to a database table
@Table(name = "employees") // Specifies the table name in the database
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all arguments
public class Employee {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies auto-increment strategy for the ID
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, unique = true) // Email should be unique for employees
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column
    private String city;

    @Column(nullable = false)
    private LocalDate dateOfJoining; // Date the employee joined

    @Column(nullable = false)
    private String branch; // Employee's assigned branch

    @Column(nullable = false)
    private String role; // Employee's role (e.g., "ADMIN", "USER")

    // For simplicity, we are not storing sensitive login credentials directly here.
    // User management (username, password) will be handled separately (potentially linked by employeeId)
    // or through Spring Security's UserDetails.
}