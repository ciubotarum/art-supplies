package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.order.orderitem.OrderItem;
import com.onlinestore.art_supplies.users.User;
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

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceOrder_Success() {
        User user = new User();
        user.setUserId(1L);

        OrderItem item1 = new OrderItem();
        item1.setPrice(BigDecimal.valueOf(10));
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setPrice(BigDecimal.valueOf(20));
        item2.setQuantity(1);

        List<OrderItem> cartItems = Arrays.asList(item1, item2);

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
    void testCalculateTotalAmount() {
        OrderItem item1 = new OrderItem();
        item1.setPrice(BigDecimal.valueOf(10));
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setPrice(BigDecimal.valueOf(20));
        item2.setQuantity(1);

        List<OrderItem> items = Arrays.asList(item1, item2);

        BigDecimal totalAmount = orderService.calculateTotalAmount(items);

        assertEquals(BigDecimal.valueOf(40), totalAmount);
    }

    @Test
    void testGetOrderHistory() {
        User user = new User();
        user.setUserId(1L);

        Order order1 = new Order();
        Order order2 = new Order();

        when(orderRepository.findByUser_UserId(1L)).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.getOrderHistory(user);

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findByUser_UserId(1L);
    }
}