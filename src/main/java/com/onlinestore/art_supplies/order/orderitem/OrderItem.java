package com.onlinestore.art_supplies.order.orderitem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.onlinestore.art_supplies.order.Order;
import com.onlinestore.art_supplies.products.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Min(value = 0, message = "Quantity cannot be less than 0")
    private Integer quantity;

    @Min(value = 0, message = "Price cannot be less than 0")
    private BigDecimal price;
}
