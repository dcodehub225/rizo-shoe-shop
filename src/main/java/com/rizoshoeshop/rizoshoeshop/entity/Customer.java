package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity // Marks this class as a JPA entity, mapping it to a database table
@Table(name = "customers") // Specifies the table name in the database
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all arguments
public class Customer {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies auto-increment strategy for the ID
    private Long id;

    @Column(nullable = false) // Specifies that this column cannot be null
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, unique = true) // Email cannot be null and must be unique
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private LocalDate dob; // Date of Birth for birthday greetings

    @Column(nullable = false)
    private String loyaltyLevel; // Customer's loyalty tier (e.g., Bronze, Silver, Gold)

    @Column(nullable = false)
    private int loyaltyPoints; // Loyalty points, updated by sales module later

    // Note: Purchase History will be handled via a relationship with Sales/Order entities later.
    // For now, we focus on basic customer profile.
}