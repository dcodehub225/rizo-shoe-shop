package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.employee.CreateEmployeeRequest;
import com.rizoshoeshop.rizoshoeshop.dto.employee.EmployeeDTO;
import com.rizoshoeshop.rizoshoeshop.dto.employee.UpdateEmployeeRequest;
import com.rizoshoeshop.rizoshoeshop.entity.Employee;
import com.rizoshoeshop.rizoshoeshop.exception.BadRequestException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.exception.ResourceNotFoundException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.repository.EmployeeRepository;
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
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class); // Add this line
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired // Injects dependencies
    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    // CREATE Operation
    public EmployeeDTO createEmployee(CreateEmployeeRequest request) {
        logger.info("Attempting to create new employee with email: {}", request.getEmail());
        // Check for duplicate employee email
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Employee creation failed: Email {} already exists.", request.getEmail());
            throw new BadRequestException("Employee with email " + request.getEmail() + " already exists."); // Changed to BadRequestException
        }

        Employee employee = modelMapper.map(request, Employee.class);
        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Employee created successfully with ID: {}", savedEmployee.getId());
        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }

    // READ Operation - Get by ID
    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class));
        // No direct exception thrown here, Optional handles not found scenario at controller level.
    }

    // READ Operation - Get all
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get by Branch
    public List<EmployeeDTO> getEmployeesByBranch(String branch) {
        return employeeRepository.findByBranch(branch).stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get by Role
    public List<EmployeeDTO> getEmployeesByRole(String role) {
        return employeeRepository.findByRole(role).stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }


    // UPDATE Operation
    public Optional<EmployeeDTO> updateEmployee(Long id, UpdateEmployeeRequest request) {
        logger.info("Attempting to update employee with ID: {}", id);
        return employeeRepository.findById(id).map(existingEmployee -> {
            // Ensure the new email is not duplicated by another employee (excluding self)
            if (employeeRepository.findByEmail(request.getEmail())
                    .map(Employee::getId)
                    .filter(existingId -> !existingId.equals(id))
                    .isPresent()) {
                logger.warn("Employee update failed: Email {} is already used by another employee.", request.getEmail());
                throw new BadRequestException("Email " + request.getEmail() + " is already used by another employee."); // Changed to BadRequestException
            }

            modelMapper.map(request, existingEmployee);
            existingEmployee.setId(id); // Ensure the ID remains the same
            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            logger.info("Employee with ID {} updated successfully.", id);
            return modelMapper.map(updatedEmployee, EmployeeDTO.class);
        });
        // No direct exception thrown here for not found, Optional handles it.
    }

    // DELETE Operation
    public boolean deleteEmployee(Long id) {
        logger.info("Attempting to delete employee with ID: {}", id);
        if (employeeRepository.existsById(id)) { // Check if employee exists
            employeeRepository.deleteById(id); // Delete by ID
            logger.info("Employee with ID {} deleted successfully.", id);
            return true;
        }
        logger.warn("Employee with ID {} not found for deletion.", id);
        return false; // Return false if not found, controller handles 404.
        // Or you could throw ResourceNotFoundException here if desired:
        // else { throw new ResourceNotFoundException("Employee not found with ID: " + id); }
    }
}