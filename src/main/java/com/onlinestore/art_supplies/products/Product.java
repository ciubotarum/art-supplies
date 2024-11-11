package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import jakarta.persistence.*;
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
    private String productName;

    private String description;
    private BigDecimal price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String image;
}
