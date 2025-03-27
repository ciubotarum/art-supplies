package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductService;
import com.onlinestore.art_supplies.ratings.RatingService;
import com.onlinestore.art_supplies.reviews.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductViewController {
    private final ProductService productService;
    private final RatingService ratingService;
    private final ReviewService reviewService;
    private final CategoryService categoryService;

    public ProductViewController(ProductService productService, RatingService ratingService, ReviewService reviewService, CategoryService categoryService) {
        this.productService = productService;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products/show")
    public String showAllProducts(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchProducts(keyword);
        } else if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategoryName(category);
        } else {
            products = productService.getAllProducts();
        }
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

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
