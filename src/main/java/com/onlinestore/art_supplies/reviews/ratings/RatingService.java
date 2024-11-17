package com.onlinestore.art_supplies.reviews.ratings;

import com.onlinestore.art_supplies.order.OrderRepository;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public RatingService(RatingRepository ratingRepository, OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Rating createRating(Integer ratingValue, Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        if (userHasOrderedProduct(user, productId)) {
            Rating rating = new Rating();
            rating.setRating(ratingValue);
            rating.setProduct(product);
            rating.setUser(user);
            return ratingRepository.save(rating);
        } else {
            throw new IllegalArgumentException("User has not ordered this product.");
        }
    }

    public List<Rating> getRatingsByProductId(Long productId) {
        return ratingRepository.findByProduct_ProductId(productId);
    }

    private boolean userHasOrderedProduct(User user, Long productId) {
        return orderRepository.existsByUserAndOrderItems_Product_ProductId(user, productId);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
