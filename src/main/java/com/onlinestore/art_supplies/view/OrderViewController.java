package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.order.Order;
import com.onlinestore.art_supplies.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderViewController {
    private final OrderService orderService;

    public OrderViewController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    public String placeOrder(HttpServletRequest request) {
        orderService.placeOrder(request);
        return "redirect:/orders";
    }

    @GetMapping
    public String viewOrders(HttpServletRequest request, Model model) {
        List<Order> orders = orderService.getOrderHistory(request);
        model.addAttribute("orders", orders);
        return "order-history";
    }
}