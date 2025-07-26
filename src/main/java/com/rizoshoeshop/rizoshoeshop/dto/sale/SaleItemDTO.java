package com.rizoshoeshop.rizoshoeshop.dto.sale;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDTO {
    private Long id;
    private Long productId; // ID of the product
    private String productName; // For display purposes
    private int quantity;
    private double unitPrice;
    private double subtotal;
}