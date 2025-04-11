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
}
