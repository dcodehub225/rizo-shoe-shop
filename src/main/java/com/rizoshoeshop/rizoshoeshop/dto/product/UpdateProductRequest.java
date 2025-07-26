package com.rizoshoeshop.rizoshoeshop.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Min(value = 0, message = "Price must be non-negative")
    private double price;

    private String imageUrl;

    // Make gender nullable by removing @NotBlank. Keep @Pattern for format validation if provided.
    private String gender;

    // Make type nullable by removing @NotBlank. Keep @Pattern for format validation if provided.
    private String type;

    // Make variety nullable by removing @NotBlank (it was already nullable in previous code)
    private String variety;

    // Make accessoryType nullable by removing @NotBlank (it was already nullable in previous code)
    private String accessoryType;

    @Min(value = 0, message = "Current stock must be non-negative")
    private int currentStock;

    @Min(value = 0, message = "Low stock threshold must be non-negative")
    private int lowStockThreshold;
}