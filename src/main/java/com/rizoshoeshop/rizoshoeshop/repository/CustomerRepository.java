package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import for Optional

@Repository // Marks this interface as a Spring Data JPA repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // JpaRepository automatically provides basic CRUD methods like save(), findById(), findAll(), deleteById().

    // Custom method to find a customer by their email
    Optional<Customer> findByEmail(String email);
    // You can add other custom finder methods here, e.g., findByPhone, findByLoyaltyLevel
}