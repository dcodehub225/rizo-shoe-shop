package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.customer.CustomerDTO;
import com.rizoshoeshop.rizoshoeshop.dto.sale.*; // Import all sale DTOs
import com.rizoshoeshop.rizoshoeshop.entity.*; // Import all entities
import com.rizoshoeshop.rizoshoeshop.repository.*; // Import all repositories
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; // For date difference calculation
import java.util.List;
import java.util.Optional;
import java.util.UUID; // For generating unique invoice number
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service // Marks this class as a Spring Service component
@Transactional // Ensures that all methods in this class run within a transaction
public class SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleService.class); // Add this line
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final RefundRepository refundRepository; // New
    private final ModelMapper modelMapper;
    private final CustomerService customerService; // To update loyalty points

    @Autowired // Injects all necessary repositories and services
    public SaleService(SaleRepository saleRepository, SaleItemRepository saleItemRepository,
                       CustomerRepository customerRepository, EmployeeRepository employeeRepository,
                       ProductRepository productRepository, RefundRepository refundRepository,
                       ModelMapper modelMapper, CustomerService customerService) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
        this.refundRepository = refundRepository;
        this.modelMapper = modelMapper;
        this.customerService = customerService;
    }

    // CREATE Sale Operation
    public SaleDTO createSale(CreateSaleRequest request) {
        logger.info("Initiating new sale for customer ID: {} by employee ID: {}", request.getCustomerId(), request.getEmployeeId());
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    logger.warn("Sale creation failed: Customer not found with ID: {}", request.getCustomerId());
                    return new RuntimeException("Customer not found with ID: " + request.getCustomerId());
                });

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> {
                    logger.warn("Sale creation failed: Employee not found with ID: {}", request.getEmployeeId());
                    return new RuntimeException("Employee not found with ID: " + request.getEmployeeId());
                });

        double totalAmount = 0;
        int loyaltyPointsEarned = 0;
        List<SaleItem> saleItems = new java.util.ArrayList<>();

        // 1. Process Sale Items, Calculate Total, Check Stock, and Deduct Stock
        for (CreateSaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemRequest.getProductId()));

            if (product.getCurrentStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product " + product.getName() + ". Available: " + product.getCurrentStock());
            }

            // Deduct stock immediately (this is inside a transaction, so it's safe)
            product.setCurrentStock(product.getCurrentStock() - itemRequest.getQuantity());
            productRepository.save(product); // Save updated product stock

            double itemSubtotal = itemRequest.getQuantity() * product.getPrice();
            totalAmount += itemSubtotal;

            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.getQuantity());
            saleItem.setUnitPrice(product.getPrice());
            saleItem.setSubtotal(itemSubtotal);
            saleItems.add(saleItem);
        }

        double netAmount = totalAmount - request.getDiscount();
        if (netAmount < 0) {
            throw new RuntimeException("Net amount cannot be negative after discount.");
        }

        // 2. Calculate Loyalty Points
        loyaltyPointsEarned = (int) (netAmount / 10.0); // Example: 1 point for every $10 spent
        customerService.updateLoyaltyPoints(customer.getId(), loyaltyPointsEarned); // Update customer's total loyalty points

        // 3. Handle Payment Details (Dummy for Card Payments)
        String cardDetails = null;
        if ("Card".equalsIgnoreCase(request.getPaymentMethod())) {
            // Simulate card payment processing
            // For a real app, this would integrate with a payment gateway.
            // Document says "Redirect card payments to dummy payment page."
            // Here, we just store a dummy detail.
            cardDetails = "Processed via Dummy Gateway: " + request.getCardPaymentDetails();
        } else if (!"Cash".equalsIgnoreCase(request.getPaymentMethod())) {
            throw new RuntimeException("Invalid payment method: " + request.getPaymentMethod());
        }

        // 4. Create Sale Record
        Sale sale = new Sale(
                "INV-" + UUID.randomUUID().toString().substring(0, 8), // Generate unique invoice number
                customer,
                employee,
                LocalDateTime.now(),
                totalAmount,
                request.getDiscount(),
                netAmount,
                request.getPaymentMethod(),
                cardDetails,
                loyaltyPointsEarned,
                "COMPLETED" // Initial status
        );

        Sale savedSale = saleRepository.save(sale);

        // 5. Link Sale Items to Sale and Save
        for (SaleItem item : saleItems) {
            item.setSale(savedSale);
            saleItemRepository.save(item);
        }
        savedSale.setSaleItems(saleItems); // Set items back to the saved sale entity

        logger.info("Sale {} created successfully for customer {}. Net amount: {}. Loyalty points earned: {}",
                savedSale.getInvoiceNo(), customer.getFirstName(), savedSale.getNetAmount(), savedSale.getLoyaltyPointsEarned());
        return modelMapper.map(savedSale, SaleDTO.class);
    }

    // READ Sale by ID
    public Optional<SaleDTO> getSaleById(Long id) {
        return saleRepository.findById(id)
                .map(this::mapSaleToDtoWithDetails); // Use helper for full mapping
    }

    // READ All Sales
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::mapSaleToDtoWithDetails)
                .collect(Collectors.toList());
    }

    // READ Sales by Customer ID
    public List<SaleDTO> getSalesByCustomerId(Long customerId) {
        return saleRepository.findByCustomer_Id(customerId).stream()
                .map(this::mapSaleToDtoWithDetails)
                .collect(Collectors.toList());
    }

    // READ Sales by Employee ID
    public List<SaleDTO> getSalesByEmployeeId(Long employeeId) {
        return saleRepository.findByEmployee_Id(employeeId).stream()
                .map(this::mapSaleToDtoWithDetails)
                .collect(Collectors.toList());
    }

    // READ Sales by Date Range
    public List<SaleDTO> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateTimeBetween(startDate, endDate).stream()
                .map(this::mapSaleToDtoWithDetails)
                .collect(Collectors.toList());
    }

    // CREATE Refund Request
    // Document: Refunds within 3 days and with tags, requires admin credentials.
    public RefundDTO createRefundRequest(RefundRequest request) {
        logger.info("Initiating refund request for Sale ID: {}", request.getSaleId());
        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> {
                    logger.warn("Refund failed: Sale not found with ID: {}", request.getSaleId());
                    return new RuntimeException("Sale not found with ID: " + request.getSaleId());
                });
        // 1. Check Refund Conditions
        long daysSinceSale = ChronoUnit.DAYS.between(sale.getSaleDateTime(), LocalDateTime.now());
        if (daysSinceSale > 3) {
            logger.warn("Refund failed for sale {}: Sale is older than 3 days. Days: {}", sale.getInvoiceNo(), daysSinceSale);
            throw new RuntimeException("Refund request denied: Sale is older than 3 days.");
        }
        if (!request.getHasTags()) {
            logger.warn("Refund failed for sale {}: Items must have tags attached.", sale.getInvoiceNo());
            throw new RuntimeException("Refund request denied: Items must have tags attached.");
        }
        if (sale.getStatus().equals("REFUNDED")) {
            logger.warn("Refund failed for sale {}: This sale has already been refunded.", sale.getInvoiceNo());
            throw new RuntimeException("Refund request denied: This sale has already been refunded.");
        }
        if (request.getRefundAmount() <= 0 || request.getRefundAmount() > sale.getNetAmount()) {
            logger.warn("Refund failed for sale {}: Invalid refund amount.", sale.getInvoiceNo());
            throw new RuntimeException("Invalid refund amount. Must be positive and not exceed net amount.");
        }

        // 2. Check Employee Role (Admin required for processing)
        Employee processedByEmployee = employeeRepository.findById(request.getProcessedByEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee processing refund not found."));
        if (!"ADMIN".equalsIgnoreCase(processedByEmployee.getRole())) {
            throw new RuntimeException("Refund processing requires Admin credentials.");
        }

        // 3. Create Refund Record
        Refund refund = modelMapper.map(request, Refund.class);
        refund.setSale(sale);
        refund.setRefundDateTime(LocalDateTime.now());
        refund.setProcessedByEmployee(processedByEmployee);
        refund.setStatus("APPROVED"); // Auto-approve if conditions met, or PENDING for manual review

        Refund savedRefund = refundRepository.save(refund);

        // 4. Update Sale Status and Stock (if refund approved)
        sale.setStatus("REFUNDED");
        saleRepository.save(sale);

        // Revert stock for refunded items (assuming full refund for now)
        for (SaleItem item : sale.getSaleItems()) {
            Product product = item.getProduct();
            product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
            productRepository.save(product);
        }

        // Reverse Loyalty Points (if applicable for full refund)
        customerService.updateLoyaltyPoints(sale.getCustomer().getId(), -sale.getLoyaltyPointsEarned());

        logger.info("Refund for sale {} processed successfully. Amount: {}", sale.getInvoiceNo(), savedRefund.getRefundAmount());
        return modelMapper.map(savedRefund, RefundDTO.class);
    }

    // Helper method to map Sale entity to SaleDTO with associated details
    private SaleDTO mapSaleToDtoWithDetails(Sale sale) {
        // IMPORTANT: Initialize LAZY collections/proxies within the transaction
        // Accessing these getters will force Hibernate to load them
        if (sale.getCustomer() != null) {
            sale.getCustomer().getId(); // Access to initialize proxy
            sale.getCustomer().getFirstName();
            sale.getCustomer().getLastName();
        }
        if (sale.getEmployee() != null) {
            sale.getEmployee().getId(); // Access to initialize proxy
            sale.getEmployee().getFirstName();
            sale.getEmployee().getLastName();
        }
        if (sale.getSaleItems() != null) {
            // Ensure saleItems collection is initialized
            sale.getSaleItems().size(); // Access to initialize collection proxy
            for (SaleItem item : sale.getSaleItems()) {
                if (item.getProduct() != null) {
                    item.getProduct().getId(); // Access to initialize product proxy
                    item.getProduct().getName();
                    item.getProduct().getProductCode();
                }
            }
        }

        // Now, map the fully initialized entity to DTO
        SaleDTO saleDTO = modelMapper.map(sale, SaleDTO.class);

        // Manual mapping for specific fields if ModelMapper still fails or for clarity
        if (sale.getCustomer() != null) {
            saleDTO.setCustomerId(sale.getCustomer().getId());
            saleDTO.setCustomerFirstName(sale.getCustomer().getFirstName());
            saleDTO.setCustomerLastName(sale.getCustomer().getLastName());
        }
        if (sale.getEmployee() != null) {
            saleDTO.setEmployeeId(sale.getEmployee().getId());
            saleDTO.setEmployeeFirstName(sale.getEmployee().getFirstName());
            saleDTO.setEmployeeLastName(sale.getEmployee().getLastName());
        }
        if (sale.getSaleItems() != null) {
            // Ensure SaleItemDTOs also have product details mapped
            saleDTO.setSaleItems(sale.getSaleItems().stream()
                    .map(item -> {
                        SaleItemDTO itemDTO = modelMapper.map(item, SaleItemDTO.class);
                        if (item.getProduct() != null) {
                            itemDTO.setProductId(item.getProduct().getId());
                            itemDTO.setProductName(item.getProduct().getName());
                        }
                        return itemDTO;
                    })
                    .collect(Collectors.toList()));
        }
        return saleDTO;
    }

    // Helper method for Refund DTO
    private RefundDTO mapRefundToDtoWithDetails(Refund refund) {
        if (refund.getProcessedByEmployee() != null) {
            refund.getProcessedByEmployee().getId(); // Initialize employee proxy
            refund.getProcessedByEmployee().getFirstName();
            refund.getProcessedByEmployee().getLastName();
        }

        RefundDTO refundDTO = modelMapper.map(refund, RefundDTO.class);
        if (refund.getSale() != null) {
            refundDTO.setSaleId(refund.getSale().getId());
        }
        if (refund.getProcessedByEmployee() != null) {
            refundDTO.setProcessedByEmployeeId(refund.getProcessedByEmployee().getId());
            refundDTO.setProcessedByEmployeeName(
                    refund.getProcessedByEmployee().getFirstName() + " " + refund.getProcessedByEmployee().getLastName()
            );
        }
        return refundDTO;
    }

    // GET Refund by Sale ID
    public Optional<RefundDTO> getRefundBySaleId(Long saleId) {
        return refundRepository.findBySale_Id(saleId)
                .map(this::mapRefundToDtoWithDetails);
    }

    // GET Refund by ID
    public Optional<RefundDTO> getRefundById(Long id) {
        return refundRepository.findById(id)
                .map(this::mapRefundToDtoWithDetails);
    }
}