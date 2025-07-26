package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findBySale_Id(Long saleId);
}