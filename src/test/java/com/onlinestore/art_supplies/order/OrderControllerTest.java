package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.order.orderitem.OrderItemRepository;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart_Success() {
        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(10));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String response = orderController.addToCart(1L, 2);

        assertEquals("Product added to cart!", response);
        assertEquals(1, orderController.getOrderItems().size());
        assertEquals(2, orderController.getOrderItems().get(0).getQuantity());
    }

    @Test
    void testAddToCart_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orderController.addToCart(1L, 2);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found with ID: 1", exception.getReason());
    }

    @Test
    void testCheckout_Success() {
        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(10));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        orderController.addToCart(1L, 2);

        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(10));
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(item);

        Order order = new Order();
        order.setOrderItems(orderItems);
        when(orderService.placeOrder(user, orderItems)).thenReturn(order);

        Order response = orderController.checkout("testUser");

        assertNotNull(response);
        assertEquals(0, orderController.getOrderItems().size());
    }

    @Test
    void testCheckout_UserNotFound() {
        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(10));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        orderController.addToCart(1L, 2);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orderController.checkout("testUser");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found with username: testUser", exception.getReason());
    }

    @Test
    void testCheckout_CartEmpty() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orderController.checkout("testUser");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cart is empty. Please add items before checking out.", exception.getReason());
    }

    @Test
    void testGetOrderHistory_Success() {
        User user = new User();
        user.setUsername("testUser");

        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(orderService.getOrderHistory(user)).thenReturn(orders);
        when(orderItemRepository.findByOrder(any(Order.class))).thenReturn(new ArrayList<>());

        List<Order> response = orderController.getOrderHistory("testUser");

        assertEquals(2, response.size());
    }

    @Test
    void testGetOrderHistory_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orderController.getOrderHistory("testUser");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found with username: testUser", exception.getReason());
    }

    @Test
    void testGetOrderItems() {
        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(10));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        orderController.addToCart(1L, 2);

        List<OrderItem> orderItems = orderController.getOrderItems();

        assertNotNull(orderItems);
        assertEquals(1, orderItems.size());
        assertEquals(2, orderItems.getFirst().getQuantity());
        assertEquals(product, orderItems.getFirst().getProduct());
    }
}