package com.rizoshoeshop.rizoshoeshop.dto.product; // Updated package name

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class ProductDTO {
    private Long id;
    private String productCode;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String gender;
    private String type;
    private String variety;
    private String accessoryType;
    private int currentStock;
    private int lowStockThreshold;
}