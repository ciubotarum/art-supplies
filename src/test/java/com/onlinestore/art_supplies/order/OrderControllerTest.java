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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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

    private Product product;
    private User user;
    private OrderItem orderItem;
    private List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(10));

        user = new User();
        user.setUsername("testUser");

        orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(10));

        orderItems = new ArrayList<>();
        orderItems.add(orderItem);
    }

    @Test
    void testAddToCart_Success() {

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
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        orderController.addToCart(1L, 2);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(orderService.placeOrder(user, orderItems)).thenReturn(new Order());

        Order response = orderController.checkout("testUser");

        assertNotNull(response);
        assertEquals(0, orderController.getOrderItems().size());
    }

    @Test
    void testCheckout_UserNotFound() {
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
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        orderController.addToCart(1L, 2);

        List<OrderItem> orderItems = orderController.getOrderItems();

        assertNotNull(orderItems);
        assertEquals(1, orderItems.size());
        assertEquals(2, orderItems.getFirst().getQuantity());
        assertEquals(product, orderItems.getFirst().getProduct());
    }
}