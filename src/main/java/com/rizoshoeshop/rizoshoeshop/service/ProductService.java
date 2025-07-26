package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.product.CreateProductRequest;
import com.rizoshoeshop.rizoshoeshop.dto.product.ProductDTO;
import com.rizoshoeshop.rizoshoeshop.dto.product.UpdateProductRequest;
import com.rizoshoeshop.rizoshoeshop.entity.Product;
import com.rizoshoeshop.rizoshoeshop.exception.BadRequestException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.exception.ResourceNotFoundException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger; // New import
import org.slf4j.LoggerFactory;

@Service // Marks this class as a Spring Service component
@Transactional // Ensures that methods in this class run within a transaction
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class); // Add this line
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired // Injects dependencies
    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    // CREATE Operation
    public ProductDTO createProduct(CreateProductRequest request) {
        logger.info("Attempting to create new product with code: {}", request.getProductCode());
        // Check for duplicate product code
        if (productRepository.findByProductCode(request.getProductCode()).isPresent()) {
            logger.warn("Product creation failed: Product code {} already exists.", request.getProductCode());
            throw new BadRequestException("Product with code " + request.getProductCode() + " already exists."); // Changed to BadRequestException
        }

        Product product = modelMapper.map(request, Product.class);
        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getId());
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    // READ Operation - Get by ID
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> modelMapper.map(product, ProductDTO.class));
        // No direct exception thrown here, Optional handles not found scenario at controller level.
    }

    // READ Operation - Get by Product Code
    public Optional<ProductDTO> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode)
                .map(product -> modelMapper.map(product, ProductDTO.class));
        // No direct exception thrown here, Optional handles not found scenario at controller level.
    }

    // READ Operation - Get all (unsorted)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get all sorted by price ascending
    public List<ProductDTO> getAllProductsSortedByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get all sorted by price descending
    public List<ProductDTO> getAllProductsSortedByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get all sorted by name ascending
    public List<ProductDTO> getAllProductsSortedByNameAsc() {
        return productRepository.findAllByOrderByNameAsc().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get products by Gender
    public List<ProductDTO> getProductsByGender(String gender) {
        return productRepository.findByGender(gender).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get products by Type (Formal/Casual/etc.)
    public List<ProductDTO> getProductsByType(String type) {
        return productRepository.findByType(type).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // READ Operation - Get low stock alerts
    public List<ProductDTO> getLowStockProducts() {
        // This method would typically find products where current stock <= lowStockThreshold
        // For simplicity, let's assume threshold is stored in the product itself.
        // In a real scenario, you might have a global default or a more complex alert system.
        return productRepository.findAll().stream()
                .filter(p -> p.getCurrentStock() <= p.getLowStockThreshold())
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // UPDATE Operation
    public Optional<ProductDTO> updateProduct(Long id, UpdateProductRequest request) {
        return productRepository.findById(id).map(existingProduct -> {
            // Product code is typically immutable. We are not updating it here.

            modelMapper.map(request, existingProduct); // Map properties from request to existing entity
            existingProduct.setId(id); // Ensure the ID remains the same
            Product updatedProduct = productRepository.save(existingProduct); // Save updated entity
            return modelMapper.map(updatedProduct, ProductDTO.class); // Map updated entity to DTO
        });
        // No direct exception thrown here for not found, Optional handles it.
    }

    // UPDATE Stock Level (This will be used by Sales Management)
    public Optional<ProductDTO> updateStock(String productCode, int quantityChange) {
        logger.info("Attempting to update stock for product {}. Change: {}", productCode, quantityChange);
        return productRepository.findByProductCode(productCode).map(product -> {
            int newStock = product.getCurrentStock() + quantityChange; // quantityChange can be negative for sales
            if (newStock < 0) {
                logger.warn("Stock update failed for product {}: Insufficient stock. Current: {}, Change: {}", productCode, product.getCurrentStock(), quantityChange);
                throw new BadRequestException("Insufficient stock for product " + productCode); // Changed to BadRequestException
            }
            product.setCurrentStock(newStock);
            Product updatedProduct = productRepository.save(product);
            logger.info("Stock for product {} updated to {}.", productCode, newStock);
            return modelMapper.map(updatedProduct, ProductDTO.class);
        });
        // No direct exception thrown here for not found, Optional handles it.
    }

    // DELETE Operation
    public boolean deleteProduct(Long id) {
        logger.info("Attempting to delete product with ID: {}", id);
        if (productRepository.existsById(id)) { // Check if product exists
            productRepository.deleteById(id); // Delete by ID
            logger.info("Product with ID {} deleted successfully.", id);
            return true;
        }
        logger.warn("Product with ID {} not found for deletion.", id);
        return false; // Return false if not found, controller handles 404.
        // Or you could throw ResourceNotFoundException here if desired:
        // else { throw new ResourceNotFoundException("Product not found with ID: " + id); }
    }
}