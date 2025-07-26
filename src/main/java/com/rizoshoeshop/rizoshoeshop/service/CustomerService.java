package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.customer.CreateCustomerRequest;
import com.rizoshoeshop.rizoshoeshop.dto.customer.CustomerDTO;
import com.rizoshoeshop.rizoshoeshop.dto.customer.UpdateCustomerRequest;
import com.rizoshoeshop.rizoshoeshop.entity.Customer;
import com.rizoshoeshop.rizoshoeshop.exception.BadRequestException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.exception.ResourceNotFoundException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service // Marks this class as a Spring Service component
@Transactional // Ensures that methods in this class run within a transaction
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper; // Used for mapping DTOs to Entities and vice-versa

    @Autowired // Injects dependencies
    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    // CREATE Operation
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        logger.info("Attempting to create new customer with email: {}", request.getEmail());
        // Check for duplicate customer email
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Customer creation failed: Email {} already exists.", request.getEmail());
            throw new BadRequestException("Customer with email " + request.getEmail() + " already exists."); // Changed to BadRequestException
        }

        Customer customer = modelMapper.map(request, Customer.class);
        // Set default loyalty level and points if not provided by the request
        if (customer.getLoyaltyLevel() == null || customer.getLoyaltyLevel().isEmpty()) {
            customer.setLoyaltyLevel("Bronze"); // Default loyalty level example
        }
        customer.setLoyaltyPoints(0); // Initialize with 0 loyalty points

        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return modelMapper.map(savedCustomer, CustomerDTO.class);
    }

    // READ Operation - Get by ID
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id) // Find by ID using the repository
                .map(customer -> modelMapper.map(customer, CustomerDTO.class)); // Map entity to DTO if found
        // No direct exception thrown here, Optional handles not found scenario at controller level.
    }

    // READ Operation - Get all
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream() // Get all customers
                .map(customer -> modelMapper.map(customer, CustomerDTO.class)) // Map each entity to DTO
                .collect(Collectors.toList()); // Collect into a list
    }

    // UPDATE Operation
    public Optional<CustomerDTO> updateCustomer(Long id, UpdateCustomerRequest request) {
        logger.info("Attempting to update customer with ID: {}", id);
        return customerRepository.findById(id).map(existingCustomer -> {
            // Ensure the new email is not duplicated by another customer (excluding self)
            if (customerRepository.findByEmail(request.getEmail())
                    .map(Customer::getId)
                    .filter(existingId -> !existingId.equals(id))
                    .isPresent()) {
                logger.warn("Customer update failed: Email {} is already used by another customer.", request.getEmail());
                throw new BadRequestException("Email " + request.getEmail() + " is already used by another customer."); // Changed to BadRequestException
            }

            modelMapper.map(request, existingCustomer); // Map properties from request to existing entity
            existingCustomer.setId(id); // Ensure the ID remains the same
            Customer updatedCustomer = customerRepository.save(existingCustomer); // Save updated entity
            logger.info("Customer with ID {} updated successfully.", id);
            return modelMapper.map(updatedCustomer, CustomerDTO.class); // Map updated entity to DTO
        });
        // No direct exception thrown here for not found, Optional handles it.
    }

    // DELETE Operation
    public boolean deleteCustomer(Long id) {
        logger.info("Attempting to delete customer with ID: {}", id);
        if (customerRepository.existsById(id)) { // Check if customer exists
            customerRepository.deleteById(id); // Delete by ID
            logger.info("Customer with ID {} deleted successfully.", id);
            return true;
        }
        logger.warn("Customer with ID {} not found for deletion.", id);
        return false; // Return false if not found, controller handles 404.
        // Or you could throw ResourceNotFoundException here if desired:
        // else { throw new ResourceNotFoundException("Customer not found with ID: " + id); }
    }

    // Method to update loyalty points separately
    public Optional<CustomerDTO> updateLoyaltyPoints(Long customerId, int pointsToAdd) {
        return customerRepository.findById(customerId).map(customer -> {
            int newPoints = customer.getLoyaltyPoints() + pointsToAdd;
            customer.setLoyaltyPoints(newPoints);
            customer.setLoyaltyLevel(determineLoyaltyLevel(newPoints)); // Determine new loyalty level based on points
            Customer updatedCustomer = customerRepository.save(customer);
            return modelMapper.map(updatedCustomer, CustomerDTO.class);
        });
        // No direct exception thrown here for not found, Optional handles it.
    }

    // Simple logic to determine loyalty tier based on points
    private String determineLoyaltyLevel(int points) {
        if (points >= 1000) {
            return "Gold";
        } else if (points >= 500) {
            return "Silver";
        } else {
            return "Bronze";
        }
    }
}