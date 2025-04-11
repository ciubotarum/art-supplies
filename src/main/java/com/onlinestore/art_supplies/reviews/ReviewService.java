package com.onlinestore.art_supplies.reviews;

import com.onlinestore.art_supplies.order.OrderRepository;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Review createReview(String reviewText, Long productId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
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

    public void deleteReview(Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with " + reviewId + " does not exist."));

        if (!review.getUser().getUserId().equals(user.getUserId()) && !user.getIsAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to delete this review.");
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
    public boolean canUserReviewProduct(User user, Long productId) {
        return userHasOrderedProduct(user, productId);
    }
}
