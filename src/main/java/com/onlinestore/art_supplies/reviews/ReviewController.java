package com.onlinestore.art_supplies.reviews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review  createdReview = reviewService.createReview(review);
        return ResponseEntity.ok(createdReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@RequestBody Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review deleted successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
}
