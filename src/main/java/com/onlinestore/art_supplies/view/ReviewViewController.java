package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.reviews.ReviewService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ReviewViewController {
    private final UserService userService;
    private final ReviewService reviewService;

    public ReviewViewController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews/add")
    public String addReview(@RequestParam String reviewText, @RequestParam Long productId, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }
        reviewService.createReview(reviewText, productId);
        return "redirect:/products/show/" + productId;
    }

    @PostMapping("/reviews/update")
    public String updateReview(@RequestParam Long reviewId, @RequestParam String reviewText, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }
        if (!reviewService.canUserEditReview(user, reviewId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this review");
        }
        reviewService.updateReview(reviewId, reviewText, user);
        Long productId = reviewService.getProductIdByReviewId(reviewId);
        return "redirect:/products/show/" + productId;
    }

    @PostMapping("/reviews/delete")
    public String deleteReview(@RequestParam Long reviewId, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in");
        }

        boolean isOwner = reviewService.canUserEditReview(user, reviewId);
        boolean isAdmin = user.getIsAdmin();

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this review");
        }

        Long productId = reviewService.getProductIdByReviewId(reviewId);
        reviewService.deleteReview(reviewId);
        return "redirect:/products/show/" + productId;
    }
}
