package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private OrderItem item1;
    private OrderItem item2;
    private List<OrderItem> cartItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        item1 = new OrderItem();
        item1.setPrice(BigDecimal.valueOf(10));
        item1.setQuantity(2);

        item2 = new OrderItem();
        item2.setPrice(BigDecimal.valueOf(20));
        item2.setQuantity(1);

        cartItems = Arrays.asList(item1, item2);
    }

    @Test
    void testPlaceOrder_Success() {

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.valueOf(40));
        order.setOrderItems(cartItems);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order placedOrder = orderService.placeOrder(user, cartItems);

        assertNotNull(placedOrder);
        assertEquals(BigDecimal.valueOf(40), placedOrder.getTotalAmount());
        assertEquals(2, placedOrder.getOrderItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCalculateTotalAmount_ZeroQuantity() {
        item1.setQuantity(0);
        BigDecimal totalAmount = orderService.calculateTotalAmount(cartItems);

        assertEquals(BigDecimal.valueOf(20), totalAmount);
    }

    @Test
    void testGetOrderHistory_NoOrders() {
        when(userService.isLoggedIn(1L)).thenReturn(true);
        when(orderRepository.findByUser_UserId(1L)).thenReturn(Arrays.asList());

        List<Order> orders = orderService.getOrderHistory(user);

        assertTrue(orders.isEmpty());
        verify(orderRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    void testCalculateTotalAmount() {

        BigDecimal totalAmount = orderService.calculateTotalAmount(cartItems);

        assertEquals(BigDecimal.valueOf(40), totalAmount);
    }

    @Test
    void testGetOrderHistory() {

        Order order1 = new Order();
        Order order2 = new Order();

        when(userService.isLoggedIn(1L)).thenReturn(true);
        when(orderRepository.findByUser_UserId(1L)).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.getOrderHistory(user);

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findByUser_UserId(1L);
    }
}