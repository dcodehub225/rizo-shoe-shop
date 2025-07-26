package com.rizoshoeshop.rizoshoeshop.dto.sale;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundDTO {
    private Long id;
    private Long saleId;
    private LocalDateTime refundDateTime;
    private double refundAmount;
    private String reason;
    private boolean hasTags;
    private Long processedByEmployeeId;
    private String processedByEmployeeName; // For display
    private String status;
}