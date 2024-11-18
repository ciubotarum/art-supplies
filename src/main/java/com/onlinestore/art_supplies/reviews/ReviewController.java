package com.onlinestore.art_supplies.reviews;

import com.onlinestore.art_supplies.dto.ReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Create a new review",
            description = "Create a new review for a product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<Review> createReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @RequestParam String username) {
        Review  createdReview = reviewService.createReview(reviewRequest.getReviewText(), reviewRequest.getProductId(), username);
        return ResponseEntity.ok(createdReview);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review",
            description = "Delete a review by review ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Review deleted"),
                    @ApiResponse(responseCode = "404", description = "Review not found")
            })
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("Review with id " + reviewId + " was deleted successfully!");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews by product ID",
            description = "Get all reviews for a product by product ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reviews found"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping
    @Operation(summary = "Get all reviews",
            description = "Get all reviews",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reviews found"),
                    @ApiResponse(responseCode = "404", description = "No reviews found")
            })
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
}
