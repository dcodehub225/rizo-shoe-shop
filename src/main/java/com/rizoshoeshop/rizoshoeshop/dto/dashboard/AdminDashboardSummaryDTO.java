package com.rizoshoeshop.rizoshoeshop.dto.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class AdminDashboardSummaryDTO {
    private double totalSalesRevenue; // Total amount of sales
    private double totalProfit; // Total profit (requires a cost price on product, which we don't have yet)
    private long totalCustomers; // Total number of customers
    private long totalEmployees; // Total number of employees
    private long totalProducts; // Total number of distinct products
    private int totalLowStockItems; // Count of items below threshold

    private List<TopSellingItemDTO> topSellingItems; // List of best-selling products
    private List<SalesTrendDTO> dailySalesTrends; // Daily sales trends for a period
}