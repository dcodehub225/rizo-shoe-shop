package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.dashboard.AdminDashboardSummaryDTO;
import com.rizoshoeshop.rizoshoeshop.dto.dashboard.SalesTrendDTO;
import com.rizoshoeshop.rizoshoeshop.dto.dashboard.TopSellingItemDTO;
import com.rizoshoeshop.rizoshoeshop.entity.Customer;
import com.rizoshoeshop.rizoshoeshop.entity.Employee;
import com.rizoshoeshop.rizoshoeshop.entity.Product;
import com.rizoshoeshop.rizoshoeshop.entity.Sale;
import com.rizoshoeshop.rizoshoeshop.entity.SaleItem;
import com.rizoshoeshop.rizoshoeshop.repository.CustomerRepository;
import com.rizoshoeshop.rizoshoeshop.repository.EmployeeRepository;
import com.rizoshoeshop.rizoshoeshop.repository.ProductRepository;
import com.rizoshoeshop.rizoshoeshop.repository.SaleItemRepository;
import com.rizoshoeshop.rizoshoeshop.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service // Marks this class as a Spring Service component
@Transactional(readOnly = true) // Methods are read-only, optimizing transactions
public class AdminDashboardService {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardService.class); // Add this line
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired // Injects necessary repositories
    public AdminDashboardService(SaleRepository saleRepository, SaleItemRepository saleItemRepository,
                                 ProductRepository productRepository, CustomerRepository customerRepository,
                                 EmployeeRepository employeeRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }


    public AdminDashboardSummaryDTO getDashboardSummary() {
        logger.info("Fetching admin dashboard summary.");
        AdminDashboardSummaryDTO summary = new AdminDashboardSummaryDTO();

        // Total Sales Revenue
        List<Sale> allSales = saleRepository.findAll();
        double totalSalesRevenue = allSales.stream()
                .mapToDouble(Sale::getNetAmount) // Use netAmount for revenue
                .sum();
        summary.setTotalSalesRevenue(totalSalesRevenue);

        // Total Profit (Placeholder - requires cost price to be accurate)
        // For now, let's assume a fixed profit margin or set to 0.
        summary.setTotalProfit(totalSalesRevenue * 0.20); // Example: 20% profit margin

        // Total Customers, Employees, Products
        summary.setTotalCustomers(customerRepository.count());
        summary.setTotalEmployees(employeeRepository.count());
        summary.setTotalProducts(productRepository.count());

        // Total Low Stock Items
        int lowStockCount = productRepository.findAll().stream()
                .filter(p -> p.getCurrentStock() <= p.getLowStockThreshold())
                .collect(Collectors.toList()).size();
        summary.setTotalLowStockItems(lowStockCount);


        // Top Selling Items (by quantity sold)
        List<SaleItem> allSaleItems = saleItemRepository.findAll();
        Map<Product, Integer> productQuantityMap = allSaleItems.stream()
                .collect(Collectors.groupingBy(SaleItem::getProduct,
                        Collectors.summingInt(SaleItem::getQuantity)));

        List<TopSellingItemDTO> topSellingItems = productQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed()) // Sort by total quantity sold (descending)
                .limit(5) // Top 5 items
                .map(entry -> {
                    Product product = entry.getKey();
                    int quantitySold = entry.getValue();
                    double revenue = allSaleItems.stream()
                            .filter(item -> item.getProduct().equals(product))
                            .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                            .sum(); // Calculate revenue for this product
                    return new TopSellingItemDTO(
                            product.getId(),
                            product.getName(),
                            product.getProductCode(),
                            quantitySold,
                            revenue
                    );
                })
                .collect(Collectors.toList());
        summary.setTopSellingItems(topSellingItems);

        // Daily Sales Trends (for the last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        LocalDateTime now = LocalDateTime.now().with(LocalTime.MAX);

        List<Sale> recentSales = saleRepository.findBySaleDateTimeBetween(thirtyDaysAgo, now);

        Map<LocalDate, List<Sale>> salesByDate = recentSales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getSaleDateTime().toLocalDate()));

        List<SalesTrendDTO> dailySalesTrends = salesByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Sale> dailySales = entry.getValue();
                    double dailyTotal = dailySales.stream().mapToDouble(Sale::getNetAmount).sum();
                    int dailyCount = dailySales.size();
                    return new SalesTrendDTO(date, dailyTotal, dailyCount);
                })
                .sorted(Comparator.comparing(SalesTrendDTO::getDate)) // Sort by date ascending
                .collect(Collectors.toList());
        summary.setDailySalesTrends(dailySalesTrends);

        logger.debug("Dashboard summary calculated. Total Revenue: {}", summary.getTotalSalesRevenue());
        return summary;
    }

    // You might add methods to get data for specific date ranges, branches, etc.
}