package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many sale items belong to one sale
    @JoinColumn(name = "sale_id", nullable = false) // Foreign key to sales table
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY) // Many sale items refer to one product
    @JoinColumn(name = "product_id", nullable = false) // Foreign key to products table
    private Product product;

    @Column(nullable = false)
    private int quantity; // Quantity of the product sold

    @Column(nullable = false)
    private double unitPrice; // Price of the product at the time of sale

    @Column(nullable = false)
    private double subtotal; // quantity * unitPrice
}