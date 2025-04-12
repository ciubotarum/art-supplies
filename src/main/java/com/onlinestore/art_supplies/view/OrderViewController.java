package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.order.Order;
import com.onlinestore.art_supplies.order.OrderService;
import com.onlinestore.art_supplies.order.cart.CartService;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderViewController {
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderViewController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/place-order")
    public String placeOrder(HttpServletRequest request, Model model) {
        try {
            orderService.placeOrder(request);
            model.addAttribute("successMessage", "Order placed successfully!");
            model.addAttribute("cartItems", cartService.getCartItems(userService.getAuthenticatedUser(request)));
            return "cart";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            model.addAttribute("cartItems", cartService.getCartItems(userService.getAuthenticatedUser(request)));
            return "cart";
        }
    }

    @GetMapping
    public String viewOrders(HttpServletRequest request, Model model) {
        List<Order> orders = orderService.getOrderHistory(request);
        model.addAttribute("orders", orders);
        return "order-history";
    }
}