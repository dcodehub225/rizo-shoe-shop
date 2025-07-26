package com.rizoshoeshop.rizoshoeshop.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleRequest {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod; // "Cash" or "Card"

    private String cardPaymentDetails; // Optional for cash payments

    @Min(value = 0, message = "Discount must be non-negative")
    private double discount;

    @NotEmpty(message = "Sale must contain at least one item")
    @Valid // Ensure validation on list elements
    private List<CreateSaleItemRequest> items;
}