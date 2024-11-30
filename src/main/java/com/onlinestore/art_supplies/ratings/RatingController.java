package com.onlinestore.art_supplies.ratings;

import com.onlinestore.art_supplies.dto.RatingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    @Operation(summary = "Create a new rating",
            description = "Create a new rating for a product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rating created"),
                    @ApiResponse(responseCode = "400", description = "User not ordered this product"),
                    @ApiResponse(responseCode = "404", description = "User or product not found")
            })
    public ResponseEntity<Rating> createRating(
            @RequestBody @Valid RatingRequest ratingRequest) {
        Rating createdRating = ratingService.createRating(
                ratingRequest.getRatingValue(),
                ratingRequest.getProductId(),
                ratingRequest.getUsername());
        return ResponseEntity.ok(createdRating);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get ratings by product ID",
            description = "Get all ratings for a product by product ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ratings found"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<List<Rating>> getRatingsByProductId(@PathVariable Long productId) {
        List<Rating> ratings = ratingService.getRatingsByProductId(productId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all ratings",
            description = "Get all ratings",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ratings found")
            })
    public ResponseEntity<List<Rating>> allRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }
}
