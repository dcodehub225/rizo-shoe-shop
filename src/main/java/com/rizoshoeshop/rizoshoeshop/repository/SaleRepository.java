package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.Sale;
import org.springframework.data.jpa.repository.EntityGraph; // Import ini
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByInvoiceNo(String invoiceNo);

    List<Sale> findByStatus(String status);


    // Fetch Sale with Customer, Employee, and SaleItems

    @EntityGraph(attributePaths = {"customer", "employee", "saleItems"})
    Optional<Sale> findById(Long id);

    @EntityGraph(attributePaths = {"customer", "employee", "saleItems"})
    List<Sale> findAll();

    @EntityGraph(attributePaths = {"customer", "employee", "saleItems"})
    List<Sale> findByCustomer_Id(Long customerId);

    @EntityGraph(attributePaths = {"customer", "employee", "saleItems"})
    List<Sale> findByEmployee_Id(Long employeeId);

    @EntityGraph(attributePaths = {"customer", "employee", "saleItems"})
    List<Sale> findBySaleDateTimeBetween(LocalDateTime start, LocalDateTime end);
}