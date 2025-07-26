package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.sale.CreateSaleRequest; // Import new DTO
import com.rizoshoeshop.rizoshoeshop.dto.sale.RefundRequest; // Import new DTO
import com.rizoshoeshop.rizoshoeshop.dto.sale.SaleDTO; // Import new DTO
import com.rizoshoeshop.rizoshoeshop.dto.sale.RefundDTO; // Import new DTO
import com.rizoshoeshop.rizoshoeshop.service.SaleService; // Import new Service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/sales") // Base URL for all endpoints in this controller
public class SaleController {

    private final SaleService saleService;

    @Autowired // Injects the SaleService
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    // Endpoint for CREATE Sale (HTTP POST to /api/sales)
    @PostMapping
    public ResponseEntity<SaleDTO> createSale(@Valid @RequestBody CreateSaleRequest request) {
        try {
            SaleDTO createdSale = saleService.createSale(request);
            return new ResponseEntity<>(createdSale, HttpStatus.CREATED); // Returns 201 Created status
        } catch (RuntimeException e) {
            // Catch business logic errors like "Customer not found", "Insufficient stock", etc.
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    // Endpoint for READ All Sales (HTTP GET to /api/sales)
    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<SaleDTO> sales = saleService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK); // Returns 200 OK status
    }

    // Endpoint for READ Sale by ID (HTTP GET to /api/sales/{id})
    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id)
                .map(saleDTO -> new ResponseEntity<>(saleDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint for READ Sales by Customer ID (HTTP GET to /api/sales/customer/{customerId})
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleDTO>> getSalesByCustomerId(@PathVariable Long customerId) {
        List<SaleDTO> sales = saleService.getSalesByCustomerId(customerId);
        if (sales.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Endpoint for READ Sales by Employee ID (HTTP GET to /api/sales/employee/{employeeId})
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SaleDTO>> getSalesByEmployeeId(@PathVariable Long employeeId) {
        List<SaleDTO> sales = saleService.getSalesByEmployeeId(employeeId);
        if (sales.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Endpoint for READ Sales by Date Range (HTTP GET to /api/sales/date-range?start=...&end=...)
    // Date format: YYYY-MM-DDTHH:MM:SS (e.g., 2025-07-25T00:00:00)
    @GetMapping("/date-range")
    public ResponseEntity<List<SaleDTO>> getSalesByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        // Parse LocalDateTime from String parameters
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        List<SaleDTO> sales = saleService.getSalesByDateRange(startDate, endDate);
        if (sales.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }


    // Endpoint for CREATE Refund Request (HTTP POST to /api/sales/refunds)
    @PostMapping("/refunds")
    public ResponseEntity<RefundDTO> createRefundRequest(@Valid @RequestBody RefundRequest request) {
        try {
            RefundDTO createdRefund = saleService.createRefundRequest(request);
            return new ResponseEntity<>(createdRefund, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(">>> RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint for GET Refund by Sale ID (HTTP GET to /api/sales/refunds/sale/{saleId})
    @GetMapping("/refunds/sale/{saleId}")
    public ResponseEntity<RefundDTO> getRefundBySaleId(@PathVariable Long saleId) {
        return saleService.getRefundBySaleId(saleId)
                .map(refundDTO -> new ResponseEntity<>(refundDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint for GET Refund by ID (HTTP GET to /api/sales/refunds/{id})
    @GetMapping("/refunds/{id}")
    public ResponseEntity<RefundDTO> getRefundById(@PathVariable Long id) {
        return saleService.getRefundById(id)
                .map(refundDTO -> new ResponseEntity<>(refundDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}