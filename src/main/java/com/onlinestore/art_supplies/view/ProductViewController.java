package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductService;
import com.onlinestore.art_supplies.ratings.RatingService;
import com.onlinestore.art_supplies.reviews.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductViewController {
    private final ProductService productService;
    private final RatingService ratingService;
    private final ReviewService reviewService;

    public ProductViewController(ProductService productService, RatingService ratingService, ReviewService reviewService) {
        this.productService = productService;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
    }

    @GetMapping("/products/show")
    public String showAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/products/show/{productId}")
    public String showProductDetails(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        Double averageRating = ratingService.getAverageRating(productId);

        System.out.println("Average Rating for Product ID " + productId + ": " + averageRating);

        model.addAttribute("product", product);
        model.addAttribute("ratings", averageRating);
        model.addAttribute("reviews", reviewService.getReviewsByProductId(productId));
        return "product-details";
    }
}
