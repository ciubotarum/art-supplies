package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final OrderService orderService;
    private final ProductRepository productRepository;

    // Temporary storage for the cart items (in-memory)
    private final List<OrderItem> cartItems = new ArrayList<>();

    public CartController(OrderService orderService, ProductRepository productRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        // Fetch the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Create a new OrderItem and add it to the cart
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());
        cartItems.add(orderItem);

        return "Product added to cart!";
    }

    @PostMapping("/checkout")
    public Order checkout(@RequestParam String username) {
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Please add items before checking out.");
        }

        // Create a new User object for demonstration purposes
        // In a real application, you would fetch the user from the database
        User user = new User();
        user.setUsername(username); // Set the username for the order

        // Place the order with the items in the cart
        Order order = orderService.placeOrder(user, cartItems);

        // Clear the cart after successful order placement
        cartItems.clear();

        return order;
    }

    @GetMapping("/history")
    public List<Order> getOrderHistory(@RequestParam String username) {
        // Create a new User object for demonstration purposes
        User user = new User();
        user.setUsername(username); // Set the username to fetch order history

        return orderService.getOrderHistory(user);
    }
}