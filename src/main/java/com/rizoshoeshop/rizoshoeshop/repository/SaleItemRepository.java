package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.SaleItem;
import org.springframework.data.jpa.repository.EntityGraph; // Import ini
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    // Fetch SaleItem with Product
    @EntityGraph(attributePaths = {"product"})
    List<SaleItem> findBySale_Id(Long saleId);

    @EntityGraph(attributePaths = {"product"})
    List<SaleItem> findByProduct_Id(Long productId);

    // Add EntityGraph to findAll() as well if you often need product details
    @Override
    @EntityGraph(attributePaths = {"product"})
    List<SaleItem> findAll();
}