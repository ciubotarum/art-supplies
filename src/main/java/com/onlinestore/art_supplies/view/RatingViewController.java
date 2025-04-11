package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.ratings.RatingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RatingViewController {
    private final RatingService ratingService;

    public RatingViewController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("products/{id}/rate")
    public String rateProduct(@PathVariable Long id, @RequestParam Integer ratingValue) {
        ratingService.createRating(ratingValue, id);
        return "redirect:/products/show/" + id;
    }
}