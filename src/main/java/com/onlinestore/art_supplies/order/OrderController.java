package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItemRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Order Controller", description = "Operations related to orders")
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;


    public OrderController(OrderService orderService, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

//    @PostMapping("/add")
//    @Operation(summary = "Add product to cart",
//            description = "Add a product to the cart by product ID and quantity",
//            parameters = {
//                    @Parameter(name = "productId", description = "The ID of the product", required = true, example = "4"),
//                    @Parameter(name = "quantity", description = "The quantity of the product", required = true, example = "2")
//            },
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Product added to cart"),
//                    @ApiResponse(responseCode = "400", description = "Incorrect quantity"),
//                    @ApiResponse(responseCode = "404", description = "Product not found")
//            })
//    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
//        orderService.addProductToCart(productId, quantity);
//        return "Product added to cart!";
//    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout",
            description = "Checkout the cart items and place an order",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user", required = true, example = "ion")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order placed"),
                    @ApiResponse(responseCode = "400", description = "There are no items in the cart"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public Order checkout(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

//        return orderService.placeOrder(user);
        return null;
    }

    @GetMapping("/history")
    @Operation(summary = "Get order history",
            description = "Get order history for a user by username",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user", required = true, example = "ion")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found"),
                    @ApiResponse(responseCode = "403", description = "User is not logged in"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public List<Order> getOrderHistory(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }
//        List<Order> orders = orderService.getOrderHistory(user);
//        for (Order order : orders) {
//            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
//            order.setOrderItems(orderItems);
//        }

//        return orders;
        return null;
    }

//    @GetMapping("/items")
//    @Operation(summary = "Get cart items",
//            description = "Get the items in the cart")
//    public List<OrderItem> getOrderItems() {
//        return orderService.getOrderItems();
//    }
}