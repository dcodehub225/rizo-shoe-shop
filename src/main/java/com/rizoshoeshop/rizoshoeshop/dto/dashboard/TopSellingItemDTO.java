package com.rizoshoeshop.rizoshoeshop.dto.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class TopSellingItemDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private int totalQuantitySold;
    private double totalRevenue;
}