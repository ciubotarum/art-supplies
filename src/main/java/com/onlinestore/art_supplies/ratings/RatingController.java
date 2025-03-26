package com.onlinestore.art_supplies.ratings;

import com.onlinestore.art_supplies.dto.RatingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rating Controller", description = "Operations related to ratings")
@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new rating",
            description = "Allows users to submit a rating for a specific product. Users can only rate products " +
                    "they have purchased.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details for creating a new rating: ratingValue, productId, username"
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rating created"),
                    @ApiResponse(responseCode = "400", description = "User not ordered this product or invalid input"),
                    @ApiResponse(responseCode = "403", description = "User is not logged in"),
                    @ApiResponse(responseCode = "404", description = "User or product not found")
            })
    public ResponseEntity<Rating> createRating(
            @RequestBody @Valid RatingRequest ratingRequest) {
        Rating createdRating = ratingService.createRating(
                ratingRequest.getRatingValue(),
                ratingRequest.getProductId());
        return ResponseEntity.ok(createdRating);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get ratings by product ID",
            description = "Fetches all ratings submitted for a specific product using the product's unique ID.",
            parameters = {
                    @Parameter(
                            name = "productId",
                            description = "The unique identifier of the product to retrieve the ratings",
                            required = true,
                            example = "8"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ratings found"),
                    @ApiResponse(responseCode = "404", description = "Ratings not found")
            })
    public ResponseEntity<List<Rating>> getRatingsByProductId(@PathVariable Long productId) {
        List<Rating> ratings = ratingService.getRatingsByProductId(productId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all ratings",
            description = "Retrieves a list of all ratings across all products.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ratings found")
            })
    public ResponseEntity<List<Rating>> allRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }
}
