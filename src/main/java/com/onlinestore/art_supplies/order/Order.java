package com.onlinestore.art_supplies.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @NotNull(message = "User cannot be null.")
    private User user;

    @NotNull(message = "Order date cannot be null.")
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Positive(message = "Total amount must be greater than 0.")
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();
}
