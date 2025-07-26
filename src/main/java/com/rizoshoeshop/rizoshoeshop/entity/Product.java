package com.rizoshoeshop.rizoshoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // Marks this class as a JPA entity
@Table(name = "products") // Specifies the table name
@Data // Lombok: Generates getters, setters, toString, etc.
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all arguments
public class Product {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false, unique = true) // Unique identifier for the product
    private String productCode; // From Shoe Code Format: FSM00001, CSW00001 [cite: 64]

    @Column(nullable = false)
    private String name; // e.g., "Men's Formal Leather Shoes"

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column
    private String imageUrl; // For displaying items with image previews

    // --- Shoe Code Format Attributes  ---
    @Column(nullable = true)
    private String gender; // M (Man), W (Woman) [cite: 59]

    @Column(nullable = true)
    private String type; // F (Formal), C (Casual), I (Industrial), S (Sport) [cite: 60]

    @Column(nullable = true)
    private String variety; // H (Heel), F (Flats), W (Wedges), FF (Flip-Flops), SD (Sandals), S (Shoes), SL (Slippers) [cite: 61]

    @Column
    private String accessoryType; // SHMP (Shampoo), POLB/POLBR/POLDBR (Polishes), SOF (Full Socks), SOH (Half Socks) [cite: 62]

    // --- Inventory Stock Attributes ---
    // For simplicity, we'll manage stock directly in Product for now.
    // For multiple sizes, a separate Stock entity with a many-to-one relationship to Product would be better.
    // Per the document's inventory sample, stock is per size, which implies a more complex relationship. [cite: 64]
    // For now, let's simplify to total stock for a product, or you can map sizes as a JSON/Map if needed.
    // Let's go with a simple total stock first, and then we can refine if sizes are critical in this phase.
    @Column(nullable = false)
    private int currentStock;

    @Column(nullable = false)
    private int lowStockThreshold; // Alert on low stock levels
}