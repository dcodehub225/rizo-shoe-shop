package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNo; // Unique invoice number for the sale

    @ManyToOne(fetch = FetchType.LAZY) // Many sales to one customer
    @JoinColumn(name = "customer_id", nullable = false) // Foreign key to customer table
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY) // Many sales to one employee (who processed the sale)
    @JoinColumn(name = "employee_id", nullable = false) // Foreign key to employee table
    private Employee employee;

    @Column(nullable = false)
    private LocalDateTime saleDateTime; // Date and time of the sale

    @Column(nullable = false)
    private double totalAmount; // Total amount of the sale

    @Column(nullable = false)
    private double discount; // Total discount applied to the sale

    @Column(nullable = false)
    private double netAmount; // totalAmount - discount

    @Column(nullable = false)
    private String paymentMethod; // Cash or Card

    @Column
    private String cardPaymentDetails; // Details for card payments (e.g., last 4 digits, transaction ID)

    @Column(nullable = false)
    private int loyaltyPointsEarned; // Loyalty points earned from this sale

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> saleItems; // List of items in this sale

    // Status of the sale (e.g., COMPLETED, REFUNDED, PENDING)
    @Column(nullable = false)
    private String status;

    // Constructor for convenience (optional, can use setters or AllArgsConstructor directly)
    public Sale(String invoiceNo, Customer customer, Employee employee, LocalDateTime saleDateTime,
                double totalAmount, double discount, double netAmount, String paymentMethod,
                String cardPaymentDetails, int loyaltyPointsEarned, String status) {
        this.invoiceNo = invoiceNo;
        this.customer = customer;
        this.employee = employee;
        this.saleDateTime = saleDateTime;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.netAmount = netAmount;
        this.paymentMethod = paymentMethod;
        this.cardPaymentDetails = cardPaymentDetails;
        this.loyaltyPointsEarned = loyaltyPointsEarned;
        this.status = status;
    }
}