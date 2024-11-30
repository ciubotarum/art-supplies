package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    @NotBlank(message = "Product name cannot be blank.")
    private String productName;

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    @Positive(message = "Price must be greater than 0.")
    private BigDecimal price;

    @Positive(message = "Quantity must be greater than 0.")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Pattern(regexp = "^(http|https)://.*$", message = "Image must be a valid URL.")
    private String image;
}
