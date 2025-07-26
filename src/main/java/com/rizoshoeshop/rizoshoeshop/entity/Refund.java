package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // One refund per one sale (or one sale can have many refunds for partials - for simplicity, one-to-one for now)
    @JoinColumn(name = "sale_id", nullable = false, unique = true) // Foreign key to sales table, unique to ensure one refund per sale
    private Sale sale;

    @Column(nullable = false)
    private LocalDateTime refundDateTime;

    @Column(nullable = false)
    private double refundAmount;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private boolean hasTags; // Flag to check if tags are present

    // Employee who processed the refund (requires admin credentials)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_employee_id", nullable = false)
    private Employee processedByEmployee;

    @Column(nullable = false)
    private String status; // e.g., PENDING, APPROVED, REJECTED
}