package com.onlinestore.art_supplies.ratings;

import com.onlinestore.art_supplies.order.OrderRepository;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import com.onlinestore.art_supplies.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingService ratingService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRating_Success() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1L);

        Product product = new Product();
        product.setProductId(1L);

        Rating rating = new Rating();
        rating.setRating(5);
        rating.setProduct(product);
        rating.setUser(user);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userService.isLoggedIn(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByUserAndOrderItems_Product_ProductId(user, 1L)).thenReturn(true);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        Rating createdRating = ratingService.createRating(5, 1L, "testUser");

        assertNotNull(createdRating);
        assertEquals(5, createdRating.getRating());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void testCreateRating_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(5, 1L, "testUser");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found with username: testUser", exception.getReason());
    }

    @Test
    void testCreateRating_ProductNotFound() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1L);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userService.isLoggedIn(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(5, 1L, "testUser");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found with id: 1", exception.getReason());
    }

    @Test
    void testCreateRating_UserHasNotOrderedProduct() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1L);

        Product product = new Product();
        product.setProductId(1L);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userService.isLoggedIn(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByUserAndOrderItems_Product_ProductId(user, 1L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(5, 1L, "testUser");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User has not ordered this product.", exception.getReason());
    }

    @Test
    void testGetRatingsByProductId_Successfully() {
        Rating rating1 = new Rating();
        Rating rating2 = new Rating();
        when(ratingRepository.findByProduct_ProductId(1L)).thenReturn(List.of(rating1, rating2));

        List<Rating> ratings = ratingService.getRatingsByProductId(1L);

        assertEquals(2, ratings.size());
        verify(ratingRepository, times(1)).findByProduct_ProductId(1L);
    }

    @Test
    void testGetRatingsByProductId_NoRatingsFound() {
        when(ratingRepository.findByProduct_ProductId(1L)).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.getRatingsByProductId(1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No ratings found for product with id: 1", exception.getReason());
    }

    @Test
    void testGetAllRatings() {
        Rating rating1 = new Rating();
        Rating rating2 = new Rating();
        when(ratingRepository.findAll()).thenReturn(List.of(rating1, rating2));

        List<Rating> ratings = ratingService.getAllRatings();

        assertEquals(2, ratings.size());
        verify(ratingRepository, times(1)).findAll();
    }
}