package com.onlinestore.art_supplies.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "Review text cannot be blank.")
    private String reviewText;

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;
}
