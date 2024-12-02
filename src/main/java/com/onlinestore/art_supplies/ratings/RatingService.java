package com.onlinestore.art_supplies.ratings;

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
public class RatingService {
    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public RatingService(RatingRepository ratingRepository, OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Rating createRating(Integer ratingValue, Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found with username: " + username));
        if (!userService.isLoggedIn(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + productId));
        if (userHasOrderedProduct(user, productId)) {
            Rating rating = new Rating();
            rating.setRating(ratingValue);
            rating.setProduct(product);
            rating.setUser(user);
            return ratingRepository.save(rating);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not ordered this product.");
        }
    }

    public List<Rating> getRatingsByProductId(Long productId) {
        List<Rating> ratings = ratingRepository.findByProduct_ProductId(productId);
        if (ratings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No ratings found for product with id: " + productId);
        }
        return ratings;
    }

    private boolean userHasOrderedProduct(User user, Long productId) {
        return orderRepository.existsByUserAndOrderItems_Product_ProductId(user, productId);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
