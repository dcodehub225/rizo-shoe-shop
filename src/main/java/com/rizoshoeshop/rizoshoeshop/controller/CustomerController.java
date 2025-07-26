package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.customer.CreateCustomerRequest;
import com.rizoshoeshop.rizoshoeshop.dto.customer.CustomerDTO;
import com.rizoshoeshop.rizoshoeshop.dto.customer.UpdateCustomerRequest;
import com.rizoshoeshop.rizoshoeshop.service.CustomerService;
import jakarta.validation.Valid; // For @Valid annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/customers") // Base URL for all endpoints in this controller
public class CustomerController {

    private final CustomerService customerService;

    @Autowired // Injects the CustomerService
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Endpoint for CREATE Customer (HTTP POST to /api/customers)
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        // @Valid: Triggers validation based on annotations in CreateCustomerRequest DTO.
        // @RequestBody: Binds the HTTP request body to the 'request' object.
        CustomerDTO createdCustomer = customerService.createCustomer(request); // Call service layer for business logic
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED); // Returns 201 Created status
    }

    // Endpoint for READ All Customers (HTTP GET to /api/customers)
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK); // Returns 200 OK status
    }

    // Endpoint for READ Customer by ID (HTTP GET to /api/customers/{id})
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        // @PathVariable: Extracts 'id' from the URL path.
        return customerService.getCustomerById(id)
                .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK)) // If found, return 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for UPDATE Customer (HTTP PUT to /api/customers/{id})
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return customerService.updateCustomer(id, request)
                .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK)) // If updated, return 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for DELETE Customer (HTTP DELETE to /api/customers/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if customer not found
    }

    // Endpoint to UPDATE Loyalty Points using PATCH
    // Example: PATCH /api/customers/{id}/loyalty?pointsToAdd=100
    @PatchMapping("/{id}/loyalty")
    public ResponseEntity<CustomerDTO> updateCustomerLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam int pointsToAdd) { // @RequestParam: Extracts 'pointsToAdd' from query parameters
        return customerService.updateLoyaltyPoints(id, pointsToAdd)
                .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}