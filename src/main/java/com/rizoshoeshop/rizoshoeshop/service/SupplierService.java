package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.supplier.CreateSupplierRequest;
import com.rizoshoeshop.rizoshoeshop.dto.supplier.SupplierDTO;
import com.rizoshoeshop.rizoshoeshop.dto.supplier.UpdateSupplierRequest;
import com.rizoshoeshop.rizoshoeshop.entity.Supplier;
import com.rizoshoeshop.rizoshoeshop.exception.BadRequestException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.exception.ResourceNotFoundException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring Service component
@Transactional // Ensures that methods in this class run within a transaction
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    @Autowired // Injects dependencies
    public SupplierService(SupplierRepository supplierRepository, ModelMapper modelMapper) {
        this.supplierRepository = supplierRepository;
        this.modelMapper = modelMapper;
    }

    // CREATE Operation
    public SupplierDTO createSupplier(CreateSupplierRequest request) {
        // Check for duplicate supplier name
        if (supplierRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Supplier with name " + request.getName() + " already exists."); // Changed to BadRequestException
        }

        Supplier supplier = modelMapper.map(request, Supplier.class);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return modelMapper.map(savedSupplier, SupplierDTO.class);
    }

    // READ Operation - Get by ID
    public Optional<SupplierDTO> getSupplierById(Long id) {
        return supplierRepository.findById(id) // Find by ID using the repository
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class)); // Map entity to DTO if found
        // No direct exception thrown here; Optional handles the not found scenario at the controller level.
    }

    // READ Operation - Get all
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream() // Get all suppliers
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class)) // Map each entity to DTO
                .collect(Collectors.toList()); // Collect into a list
    }

    // UPDATE Operation
    public Optional<SupplierDTO> updateSupplier(Long id, UpdateSupplierRequest request) {
        return supplierRepository.findById(id).map(existingSupplier -> {
            // No duplicate name check for update on the same supplier.
            // If you want to check if the new name duplicates another supplier, you'd add:
            // if (supplierRepository.findByName(request.getName()).filter(s -> !s.getId().equals(id)).isPresent()) {
            //     throw new BadRequestException("Supplier with name " + request.getName() + " already exists.");
            // }

            modelMapper.map(request, existingSupplier); // Map properties from request to existing entity
            existingSupplier.setId(id); // Ensure the ID remains the same
            Supplier updatedSupplier = supplierRepository.save(existingSupplier); // Save updated entity
            return modelMapper.map(updatedSupplier, SupplierDTO.class); // Map updated entity to DTO
        });
        // No direct exception thrown here for not found; Optional handles it at the controller level.
    }

    // DELETE Operation
    public boolean deleteSupplier(Long id) {
        if (supplierRepository.existsById(id)) { // Check if supplier exists
            supplierRepository.deleteById(id); // Delete by ID
            return true;
        }
        return false; // Return false if not found, controller handles 404.
        // Alternatively, you could throw ResourceNotFoundException here:
        // else { throw new ResourceNotFoundException("Supplier not found with ID: " + id); }
    }
}