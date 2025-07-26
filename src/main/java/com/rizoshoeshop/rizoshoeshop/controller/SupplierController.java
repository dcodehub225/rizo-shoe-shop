package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.supplier.CreateSupplierRequest;
import com.rizoshoeshop.rizoshoeshop.dto.supplier.SupplierDTO;
import com.rizoshoeshop.rizoshoeshop.dto.supplier.UpdateSupplierRequest;
import com.rizoshoeshop.rizoshoeshop.service.SupplierService;
import jakarta.validation.Valid; // For @Valid annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/suppliers") // Base URL for all endpoints in this controller
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired // Injects the SupplierService
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // Endpoint for CREATE Supplier (HTTP POST to /api/suppliers)
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        // @Valid: Triggers validation based on annotations in CreateSupplierRequest DTO.
        // @RequestBody: Binds the HTTP request body to the 'request' object.
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        SupplierDTO createdSupplier = supplierService.createSupplier(request); // Call service layer for business logic
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED); // Returns 201 Created status
    }

    // Endpoint for READ All Suppliers (HTTP GET to /api/suppliers)
    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return new ResponseEntity<>(suppliers, HttpStatus.OK); // Returns 200 OK status
    }

    // Endpoint for READ Supplier by ID (HTTP GET to /api/suppliers/{id})
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        // @PathVariable: Extracts 'id' from the URL path.
        return supplierService.getSupplierById(id)
                .map(supplierDTO -> new ResponseEntity<>(supplierDTO, HttpStatus.OK)) // If found, return 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for UPDATE Supplier (HTTP PUT to /api/suppliers/{id})
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody UpdateSupplierRequest request) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        return supplierService.updateSupplier(id, request)
                .map(supplierDTO -> new ResponseEntity<>(supplierDTO, HttpStatus.OK)) // If updated, return 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for DELETE Supplier (HTTP DELETE to /api/suppliers/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        boolean deleted = supplierService.deleteSupplier(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if supplier not found
    }
}