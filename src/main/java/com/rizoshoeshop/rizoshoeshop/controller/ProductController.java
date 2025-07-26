package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.product.CreateProductRequest;
import com.rizoshoeshop.rizoshoeshop.dto.product.ProductDTO;
import com.rizoshoeshop.rizoshoeshop.dto.product.UpdateProductRequest;
import com.rizoshoeshop.rizoshoeshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/products") // Base URL for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    @Autowired // Injects the ProductService
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoint for CREATE Product (HTTP POST to /api/products)
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        ProductDTO createdProduct = productService.createProduct(request);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created status
    }

    // Endpoint for READ All Products (HTTP GET to /api/products) - unsorted
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK); // Returns 200 OK status
    }

    // Endpoint for READ Product by ID (HTTP GET to /api/products/{id})
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for READ Product by Product Code (HTTP GET to /api/products/code/{productCode})
    @GetMapping("/code/{productCode}")
    public ResponseEntity<ProductDTO> getProductByCode(@PathVariable String productCode) {
        return productService.getProductByCode(productCode)
                .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoints for sorting requirements [cite: 37]
    // GET /api/products/sorted?sortBy=priceAsc
    // GET /api/products/sorted?sortBy=priceDesc
    // GET /api/products/sorted?sortBy=nameAsc
    @GetMapping("/sorted")
    public ResponseEntity<List<ProductDTO>> getProductsSorted(@RequestParam String sortBy) {
        List<ProductDTO> products;
        switch (sortBy) {
            case "priceAsc": products = productService.getAllProductsSortedByPriceAsc(); break;
            case "priceDesc": products = productService.getAllProductsSortedByPriceDesc(); break;
            case "nameAsc": products = productService.getAllProductsSortedByNameAsc(); break;
            default: return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid sort parameter
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoints for filtering requirements
    // GET /api/products/gender/{gender}
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<ProductDTO>> getProductsByGender(@PathVariable String gender) {
        List<ProductDTO> products = productService.getProductsByGender(gender);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // GET /api/products/type/{type} (Formal/Casual)
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProductDTO>> getProductsByType(@PathVariable String type) {
        List<ProductDTO> products = productService.getProductsByType(type);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint for low stock alerts
    // GET /api/products/low-stock
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        List<ProductDTO> products = productService.getLowStockProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint for UPDATE Product (HTTP PUT to /api/products/{id})
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        return productService.updateProduct(id, request)
                .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for UPDATE Product Stock (HTTP PATCH to /api/products/stock/{productCode})
    // Used typically for sales/returns. Quantity change can be negative for sales.
    @PatchMapping("/stock/{productCode}")
    public ResponseEntity<ProductDTO> updateProductStock(
            @PathVariable String productCode,
            @RequestParam int quantityChange) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException
        return productService.updateStock(productCode, quantityChange)
                .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint for DELETE Product (HTTP DELETE to /api/products/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if product not found
    }
}