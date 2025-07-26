package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.employee.CreateEmployeeRequest;
import com.rizoshoeshop.rizoshoeshop.dto.employee.EmployeeDTO;
import com.rizoshoeshop.rizoshoeshop.dto.employee.UpdateEmployeeRequest;
import com.rizoshoeshop.rizoshoeshop.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/employees") // Base URL for all endpoints in this controller
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired // Injects the EmployeeService
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Endpoint for CREATE Employee (HTTP POST to /api/employees)
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        EmployeeDTO createdEmployee = employeeService.createEmployee(request);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED); // Returns 201 Created status
    }

    // Endpoint for READ All Employees (HTTP GET to /api/employees)
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK); // Returns 200 OK status
    }

    // Endpoint for READ Employee by ID (HTTP GET to /api/employees/{id})
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(employeeDTO -> new ResponseEntity<>(employeeDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for READ Employees by Branch (HTTP GET to /api/employees/branch/{branchName})
    @GetMapping("/branch/{branch}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByBranch(@PathVariable String branch) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByBranch(branch);
        if (employees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    // Endpoint for READ Employees by Role (HTTP GET to /api/employees/role/{roleName})
    @GetMapping("/role/{role}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByRole(@PathVariable String role) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByRole(role);
        if (employees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    // Endpoint for UPDATE Employee (HTTP PUT to /api/employees/{id})
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest request) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        return employeeService.updateEmployee(id, request)
                .map(employeeDTO -> new ResponseEntity<>(employeeDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for DELETE Employee (HTTP DELETE to /api/employees/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.deleteEmployee(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if employee not found
    }
}