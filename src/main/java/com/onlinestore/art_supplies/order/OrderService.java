package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.cart.CartItem;
import com.onlinestore.art_supplies.order.cart.CartService;
import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, UserService userService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.cartService = cartService;
    }

    @Transactional
    public Order placeOrder(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setOrder(order);

            System.out.println("CartItem: " + cartItem.getProduct().getProductName() + " - " + cartItem.getQuantity());
            System.out.println("OrderItem: " + orderItem.getProduct().getProductName() + " - " + orderItem.getQuantity());


            return orderItem;
        }).toList();

        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));

        orderRepository.save(order);

        cartService.clearCart(user);
        return order;
    }


    public BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Order> getOrderHistory(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        if (user != null) {
            return orderRepository.findByUser_UserId(user.getUserId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }
    }
}
