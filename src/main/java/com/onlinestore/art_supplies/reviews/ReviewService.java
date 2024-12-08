package com.onlinestore.art_supplies.reviews;

import com.onlinestore.art_supplies.order.OrderRepository;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import com.onlinestore.art_supplies.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Review createReview(String reviewText, Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
        if (!userService.isLoggedIn(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + productId));

        if (userHasOrderedProduct(user, productId)) {
            Review review = new Review();
            review.setReviewText(reviewText);
            review.setProduct(product);
            review.setUser(user);
            return reviewRepository.save(review);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not ordered this product.");
        }
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with " + reviewId + " does not exist."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " does not exist."));

        if (!userService.isLoggedIn(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in.");
        }
        try {
            userService.checkAdminAndLoggedIn(userId);
        } catch (ResponseStatusException e) {
            if (!review.getUser().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to delete this review.");
            }
        }
        reviewRepository.deleteById(reviewId);
    }

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findReviewByProduct_ProductId(productId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    private boolean userHasOrderedProduct(User user, Long productId) {
        return orderRepository.existsByUserAndOrderItems_Product_ProductId(user, productId);
    }
}
