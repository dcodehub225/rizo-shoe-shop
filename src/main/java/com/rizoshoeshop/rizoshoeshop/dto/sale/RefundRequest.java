package com.rizoshoeshop.rizoshoeshop.dto.sale;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    @NotNull(message = "Sale ID cannot be null")
    private Long saleId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Refund amount must be non-negative")
    private Double refundAmount;

    @NotBlank(message = "Reason for refund cannot be blank")
    private String reason;

    @NotNull(message = "Has tags status cannot be null")
    private Boolean hasTags; // Flag to check if tags are present

    @NotNull(message = "Employee ID processing refund cannot be null")
    private Long processedByEmployeeId; // Employee processing the refund (must be admin)
}