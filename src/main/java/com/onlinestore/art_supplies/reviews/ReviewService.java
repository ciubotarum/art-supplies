package com.onlinestore.art_supplies.reviews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
        } else {
            throw new IllegalArgumentException("Review with " + reviewId + " does not exist.");
        }
    }

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findReviewByProductId(productId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
