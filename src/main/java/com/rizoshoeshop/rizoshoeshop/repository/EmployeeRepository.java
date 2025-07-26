package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // JpaRepository automatically provides basic CRUD methods.

    // Custom method to find an employee by their email (should be unique)
    Optional<Employee> findByEmail(String email);

    // Custom method to find employees by their assigned branch
    List<Employee> findByBranch(String branch);

    // Custom method to find employees by their role
    List<Employee> findByRole(String role);
}