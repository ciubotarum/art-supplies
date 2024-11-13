package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.order.orderitem.OrderItemRepository;
import com.onlinestore.art_supplies.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Order placeOrder(User user, List<OrderItem> items) {
        if (user == null) {
            throw new IllegalArgumentException("User must be logged in to place an order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotalAmount(items));
        orderRepository.save(order);

        for (OrderItem item : items) {
            item.setOrder(order);
            orderItemRepository.save(item);
        }

        return order;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public List<Order> getOrderHistory(User user) {
        return orderRepository.findByUser_UserId(user.getUserId());
    }
}
