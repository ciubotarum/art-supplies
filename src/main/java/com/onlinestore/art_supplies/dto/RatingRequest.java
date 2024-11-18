package com.onlinestore.art_supplies.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {
    @Min(value = 1, message = "Rating value must be between 1 and 5.")
    @Max(value = 5, message = "Rating value must be between 1 and 5.")
    private Integer ratingValue;

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotBlank(message = "Username cannot be blank or null.")
    private String username;
}
