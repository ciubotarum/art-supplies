package com.onlinestore.art_supplies.reviews;

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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReview_Success() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1L);

        Product product = new Product();
        product.setProductId(1L);

        Review review = new Review();
        review.setReviewText("Great product!");
        review.setProduct(product);
        review.setUser(user);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByUserAndOrderItems_Product_ProductId(user, 1L)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(userService.isLoggedIn(1L)).thenReturn(true);

        Review createdReview = reviewService.createReview("Great product!", 1L, "testUser");

        assertNotNull(createdReview);
        assertEquals("Great product!", createdReview.getReviewText());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReview_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview("Great product!", 1L, "testUser"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found with username: testUser", exception.getReason());
    }

    @Test
    void testCreateReview_ProductNotFound() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1L);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.isLoggedIn(1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview("Great product!", 1L, "testUser"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found with id: 1", exception.getReason());
    }

    @Test
    void testCreateReview_UserHasNotOrderedProduct() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserId(1L);
        Product product = new Product();
        product.setProductId(1L);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByUserAndOrderItems_Product_ProductId(user, 1L)).thenReturn(false);
        when(userService.isLoggedIn(1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reviewService.createReview("Great product!", 1L, "testUser");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User has not ordered this product.", exception.getReason());
    }

    @Test
    void testDeleteReview_Success() {
        Long reviewId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        Review review = new Review();
        review.setReviewId(reviewId);
        review.setUser(user);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.isLoggedIn(userId)).thenReturn(true);

        reviewService.deleteReview(reviewId, userId);

        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    void testDeleteReview_NotFound() {
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reviewService.deleteReview(reviewId, userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Review with " + reviewId + " does not exist.", exception.getReason());
    }

    @Test
    void testGetReviewsByProductId() {
        Review review1 = new Review();
        Review review2 = new Review();
        when(reviewRepository.findReviewByProduct_ProductId(1L)).thenReturn(Arrays.asList(review1, review2));

        List<Review> reviews = reviewService.getReviewsByProductId(1L);

        assertEquals(2, reviews.size());
        verify(reviewRepository, times(1)).findReviewByProduct_ProductId(1L);
    }

    @Test
    void testGetAllReviews() {
        Review review1 = new Review();
        Review review2 = new Review();
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2));

        List<Review> reviews = reviewService.getAllReviews();

        assertEquals(2, reviews.size());
        verify(reviewRepository, times(1)).findAll();
    }
}