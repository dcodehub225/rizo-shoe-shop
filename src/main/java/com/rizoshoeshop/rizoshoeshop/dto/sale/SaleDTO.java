package com.rizoshoeshop.rizoshoeshop.dto.sale;

import com.rizoshoeshop.rizoshoeshop.dto.customer.CustomerDTO;
import com.rizoshoeshop.rizoshoeshop.dto.employee.EmployeeDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long id;
    private String invoiceNo;
    private Long customerId; // Just the ID
    private String customerFirstName; // For display
    private String customerLastName; // For display
    private Long employeeId; // Just the ID
    private String employeeFirstName; // For display
    private String employeeLastName; // For display
    private LocalDateTime saleDateTime;
    private double totalAmount;
    private double discount;
    private double netAmount;
    private String paymentMethod;
    private String cardPaymentDetails;
    private int loyaltyPointsEarned;
    private String status;
    private List<SaleItemDTO> saleItems; // List of items in this sale
}