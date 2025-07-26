package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom method to find a product by its unique product code
    Optional<Product> findByProductCode(String productCode);

    // Custom methods for sorting/filtering requirements [cite: 37]
    List<Product> findAllByOrderByPriceAsc(); // Sort by Price Ascending [cite: 38]
    List<Product> findAllByOrderByPriceDesc(); // Sort by Price Descending [cite: 38]
    List<Product> findAllByOrderByNameAsc(); // Sort by Name Ascending [cite: 39]
    List<Product> findByGender(String gender); // Sort/Filter by Gender [cite: 40]
    List<Product> findByType(String type); // Sort/Filter by Formal/Casual Type [cite: 41]

    // Method to find products below low stock threshold
    List<Product> findByCurrentStockLessThanEqual(int threshold);
}