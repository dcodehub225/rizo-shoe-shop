package com.rizoshoeshop.rizoshoeshop.dto.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class SalesTrendDTO {
    private LocalDate date; // Or String for month/year (e.g., "2025-07")
    private double totalSalesAmount;
    private int totalSalesCount;
}