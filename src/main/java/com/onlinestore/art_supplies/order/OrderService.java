package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Transactional
    public Order placeOrder(User user, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotalAmount(orderItems));
        order.setOrderItems(orderItems);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order = orderRepository.save(order);
        return order;
    }

    public BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Order> getOrderHistory(User user) {
        if (userService.isLoggedIn(user.getUserId())) {
            return orderRepository.findByUser_UserId(user.getUserId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }
    }
}
