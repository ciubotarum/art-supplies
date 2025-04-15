package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductService;
import com.onlinestore.art_supplies.ratings.RatingService;
import com.onlinestore.art_supplies.reviews.Review;
import com.onlinestore.art_supplies.reviews.ReviewService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductViewController {
    private final ProductService productService;
    private final RatingService ratingService;
    private final ReviewService reviewService;
    private final CategoryService categoryService;
    private final UserService userService;

    public ProductViewController(ProductService productService, RatingService ratingService, ReviewService reviewService, CategoryService categoryService, UserService userService) {
        this.productService = productService;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/products/show")
    public String showAllProducts(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String categoryName,
                                  @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                  Model model) {
        Page<Product> productPage = productService.getFilteredProducts(page, keyword, categoryName);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categoryName);

        return "products";
    }

    @GetMapping("/products/show/{productId}")
    public String showProductDetails(@PathVariable Long productId, @RequestParam(required = false) Long editingReviewId, Model model, HttpServletRequest request) {
        Product product = productService.getProductById(productId);
        User user = userService.getAuthenticatedUser(request);
        Double averageRating = ratingService.getAverageRating(productId);

        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        Map<Long, Boolean> canEditMap = new HashMap<>();
        Map<Long, Boolean> canDeleteMap = new HashMap<>();

        for (Review review : reviews) {
            boolean canEdit = (user != null) && reviewService.canUserEditReview(user, review.getReviewId());
            boolean isAdmin = user != null && user.getIsAdmin();
            canEditMap.put(review.getReviewId(), canEdit);
            canDeleteMap.put(review.getReviewId(), canEdit || isAdmin);
        }

        boolean canReview = user != null && reviewService.canUserReviewProduct(user, productId);
        boolean canRate = user != null && ratingService.canUserRateProduct(user, productId);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("canReview", canReview);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("canRate", canRate);
        model.addAttribute("canEditMap", canEditMap);
        model.addAttribute("editingReviewId", editingReviewId);
        model.addAttribute("canDeleteMap", canDeleteMap);
        return "product-details";
    }

    @GetMapping("/products/show/filter")
    public @ResponseBody List<Product> filterProducts(@RequestParam("categoryName") String categoryName) {
        if ("all".equalsIgnoreCase(categoryName)) {
            return productService.getAllProducts();
        }
        return productService.getProductsByCategoryName(categoryName);
    }

    @GetMapping("/products/show/search")
    public @ResponseBody List<Product> searchProducts(@RequestParam("keyword") String keyword) {
        return productService.searchProducts(keyword);
    }
}
