package com.onlinestore.art_supplies.ratings;

import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.reviews.ReviewService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewService reviewService;


    public RatingService(RatingRepository ratingRepository, ProductRepository productRepository, UserRepository userRepository, ReviewService reviewService) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
    }

    public Rating createRating(Integer ratingValue, Long productId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + productId));
        if (reviewService.userHasOrderedProduct(user, productId)) {
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
        return ratingRepository.findByProduct_ProductId(productId);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Double getAverageRating(Long productId) {
        List<Rating> ratings = getRatingsByProductId(productId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        double totalRating = 0;
        for (Rating rating : ratings) {
            totalRating += rating.getRating();
        }
        return totalRating / ratings.size();
    }

    public boolean canUserRateProduct(User user, Long productId) {
        return reviewService.userHasOrderedProduct(user, productId);
    }
}
