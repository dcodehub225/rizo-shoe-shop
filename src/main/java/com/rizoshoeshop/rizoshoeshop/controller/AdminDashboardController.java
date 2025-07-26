package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.dashboard.AdminDashboardSummaryDTO;
import com.rizoshoeshop.rizoshoeshop.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/dashboard") // Base URL for dashboard endpoints
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Autowired // Injects the AdminDashboardService
    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    // Endpoint to get the overall admin dashboard summary
    // HTTP GET to /api/dashboard/summary
    @GetMapping("/summary")
    public ResponseEntity<AdminDashboardSummaryDTO> getDashboardSummary() {
        AdminDashboardSummaryDTO summary = adminDashboardService.getDashboardSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    // You might add more specific endpoints here for different reports
    // For instance: getTopSellingItems (if needed separately), getSalesTrends (with date params)
}