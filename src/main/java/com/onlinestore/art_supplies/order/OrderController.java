package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.order.orderitem.OrderItemRepository;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    // Temporary storage for the cart items (in-memory)
    private final List<OrderItem> cartItems = new ArrayList<>();

    public OrderController(OrderService orderService, ProductRepository productRepository, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @PostMapping("/add")
    @Operation(summary = "Add product to cart",
            description = "Add a product to the cart by product ID and quantity",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added to cart"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        // Fetch the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId));

        // Check if the product is already in the cart
        for (OrderItem item : cartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return "Product quantity updated in cart!";
            }
        }

        // Create a new OrderItem and add it to the cart
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());
        cartItems.add(orderItem);

        return "Product added to cart!";
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout",
            description = "Checkout the cart items and place an order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order placed"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public Order checkout(@RequestParam String username) {
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty. Please add items before checking out.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));

        Order order = orderService.placeOrder(user, cartItems);
        cartItems.clear();

        return order;
    }

    @GetMapping("/history")
    @Operation(summary = "Get order history",
            description = "Get order history for a user by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public List<Order> getOrderHistory(@RequestParam String username) {
        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));

        List<Order> orders = orderService.getOrderHistory(user);
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            order.setOrderItems(orderItems);
        }

        return orders;
    }

    @GetMapping("/items")
    @Operation(summary = "Get cart items",
            description = "Get the items in the cart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart items found"),
                    @ApiResponse(responseCode = "404", description = "Cart is empty")
            })
    public List<OrderItem> getCartItems() {
        return cartItems;
    }
}